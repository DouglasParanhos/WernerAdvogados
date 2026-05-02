#!/usr/bin/env python3
"""
Extract and unify PJE movement reports from a .docx into a single Markdown file.
"""
from __future__ import annotations

import argparse
import re
import sys
import zipfile
import xml.etree.ElementTree as ET
from dataclasses import dataclass, field
from datetime import datetime
from pathlib import Path

NS = {"w": "http://schemas.openxmlformats.org/wordprocessingml/2006/main"}


def _text_from_runs(elem: ET.Element) -> str:
    parts: list[str] = []
    for t in elem.findall(".//w:t", NS):
        if t.text:
            parts.append(t.text)
        if t.tail:
            parts.append(t.tail)
    return "".join(parts)


def extract_docx_paragraphs(path: Path) -> list[str]:
    with zipfile.ZipFile(path) as z:
        xml_bytes = z.read("word/document.xml")
    root = ET.fromstring(xml_bytes)
    body = root.find("w:body", NS)
    if body is None:
        return []
    out: list[str] = []
    for child in body:
        tag = child.tag.split("}")[-1]
        if tag == "p":
            line = _text_from_runs(child).strip()
            if line:
                out.append(line)
        elif tag == "tbl":
            rows: list[list[str]] = []
            for tr in child.findall(".//w:tr", NS):
                cells = []
                for tc in tr.findall("w:tc", NS):
                    cells.append(_text_from_runs(tc).strip())
                if cells:
                    rows.append(cells)
            if not rows:
                continue
            # Render table as lines (markdown-like)
            for row in rows:
                out.append(" | ".join(row))
    return out


# Portuguese month abbreviations (Niterói block)
MONTHS_PT = {
    "jan": 1,
    "fev": 2,
    "mar": 3,
    "abr": 4,
    "mai": 5,
    "jun": 6,
    "jul": 7,
    "ago": 8,
    "set": 9,
    "out": 10,
    "nov": 11,
    "dez": 12,
}

RE_DDMMYYYY = re.compile(r"\b(\d{2})/(\d{2})/(\d{4})\b")
RE_DATE_ANY = re.compile(r"\d{2}/\d{2}/\d{4}")
RE_DATADESC_PREFIX = re.compile(r"^DataDescri[cç][aã]o", re.I)
RE_PROCESSO = re.compile(
    r"^(?P<num>\d+)\.\s+Processo n[º°]\s+(?P<cnj>[\d.-]+)", re.I
)
RE_PROCESSO_ALT = re.compile(
    r"^Processo n[º°]\s+(?P<cnj>[\d.-]+)", re.I
)
RE_HEADING_NITER = re.compile(
    r"^##\s+(?P<idx>\d+)\.\s+(?P<tipo>\S+)\s+(?P<cnj>[\d.-]+)\s*$"
)
# TJRJ "RELATÓRIO DE ANDAMENTOS PROCESSUAIS" — PROCESSO N – CNJ (en-dash U+2013, em-dash U+2014)
RE_PROCESO_TJRJ = re.compile(
    r"^PROCESSO\s+\d+\s*[\u2013\u2014\-]\s*([\d.-]+)", re.I
)


@dataclass
class ProcessBlock:
    comarca: str
    cnj: str
    tipo: str | None
    partes: str | None = None
    vara: str | None = None
    distribuido: str | None = None
    movements: list[tuple[str, str]] = field(default_factory=list)  # (dd/mm/yyyy, desc)
    niteroi_filters_note: str | None = None
    niteroi_meta: dict[str, str] | None = None


def parse_pt_date(s: str) -> tuple[int, int, int] | None:
    s = s.strip()
    m = re.match(
        r"^(\d{1,2})\s+([a-zç]{3})\s+(\d{4})$",
        s,
        re.I,
    )
    if not m:
        return None
    d, mon, y = m.groups()
    mon_key = mon.lower()[:3]
    if mon_key not in MONTHS_PT:
        return None
    return int(y), MONTHS_PT[mon_key], int(d)


def parse_ddmmyyyy(s: str) -> tuple[int, int, int] | None:
    m = RE_DDMMYYYY.search(s)
    if not m:
        return None
    d, mo, y = int(m.group(1)), int(m.group(2)), int(m.group(3))
    try:
        datetime(y, mo, d)
    except ValueError:
        return None
    return y, mo, d


def sort_key_date(ddmmyyyy: str) -> tuple[int, int, int]:
    p = parse_ddmmyyyy(ddmmyyyy)
    if p:
        y, m, d = p
        return (y, m, d)
    return (9999, 12, 31)


def _is_movement_date_start(blob: str, idx: int) -> bool:
    """
    True if date at idx starts a new movement.

    - Glued after 4-digit year (e.g. ...202529/04/2025 or ...202305/12/2023) is a boundary.
    - Date not preceded by a digit splits new movement (e.g. ...sorteio08/02/2023).
    - Reject dates right after 'em ' (e.g. 'Intimação em 14/04/2025').
    """
    if idx == 0:
        return True
    if idx >= 4 and blob[idx - 4 : idx].isdigit():
        return True
    if idx > 0 and blob[idx - 1].isdigit():
        return False
    prefix = blob[:idx]
    if re.search(r"\bem\s*$", prefix, re.I):
        return False
    return True


def split_datadescricao(blob: str) -> list[tuple[str, str]]:
    """Split 'DataDescrição13/09/2024Foo13/09/2024Bar' into (date, desc) pairs."""
    blob = blob.strip()
    blob = RE_DATADESC_PREFIX.sub("", blob)
    matches = [m for m in RE_DATE_ANY.finditer(blob) if _is_movement_date_start(blob, m.start())]
    if not matches:
        return []
    pairs: list[tuple[str, str]] = []
    for i, m in enumerate(matches):
        ds = m.group(0)
        start = m.end()
        end = matches[i + 1].start() if i + 1 < len(matches) else len(blob)
        desc = blob[start:end].strip()
        pairs.append((ds, desc))
    return pairs


def parse_niteroi_table_lines(lines: list[str], start: int) -> tuple[list[tuple[str, str]], int]:
    """Parse markdown table after ## heading; returns movements and next index."""
    movements: list[tuple[str, str]] = []
    i = start
    if i < len(lines) and lines[i].strip() == "":
        i += 1
    # skip header | Data | Descrição |
    if i < len(lines) and "|" in lines[i] and "Data" in lines[i]:
        i += 1
    if i < len(lines) and re.match(r"^\|[\s\-:|]+\|$", lines[i].replace(" ", "")):
        i += 1
    while i < len(lines):
        line = lines[i].strip()
        if not line.startswith("|"):
            break
        if line.startswith("|") and "Data" in line and "---" not in line:
            # duplicate header
            i += 1
            continue
        parts = [p.strip() for p in line.split("|")]
        parts = [p for p in parts if p != ""]
        if len(parts) >= 2 and parts[0].lower() not in ("data", "---"):
            date_raw, desc = parts[0], " | ".join(parts[1:])
            pt = parse_pt_date(date_raw)
            if pt:
                y, mo, d = pt
                ddmmyyyy = f"{d:02d}/{mo:02d}/{y:04d}"
                movements.append((ddmmyyyy, desc))
        i += 1
    return movements, i


def infer_comarca_from_vara(vara: str | None) -> str | None:
    if not vara:
        return None
    m = re.search(r"Comarca d[ae]\s+([^(\n]+?)(?:\s*$|\()", vara, re.I)
    if m:
        return m.group(1).strip()
    return None


def _normalize_partial_date(s: str) -> str | None:
    """Convert MM/YYYY or DD/MM/YYYY to DD/MM/YYYY. Returns None if invalid."""
    s = s.strip()
    # DD/MM/YYYY
    m = re.match(r"^(\d{1,2})/(\d{1,2})/(\d{4})$", s)
    if m:
        d, mo, y = int(m.group(1)), int(m.group(2)), int(m.group(3))
        try:
            datetime(y, mo, d)
            return f"{d:02d}/{mo:02d}/{y:04d}"
        except ValueError:
            return None
    # MM/YYYY
    m = re.match(r"^(\d{1,2})/(\d{4})$", s)
    if m:
        mo, y = int(m.group(1)), int(m.group(2))
        try:
            datetime(y, mo, 1)
            return f"01/{mo:02d}/{y:04d}"
        except ValueError:
            return None
    return None


def parse_tjrj_table_lines(lines: list[str], start: int) -> tuple[list[tuple[str, str]], int]:
    """Parse TJRJ format: 'Data | Movimento' header then 'DD/MM/YYYY | desc' rows."""
    movements: list[tuple[str, str]] = []
    i = start
    # Skip blank and header "Data | Movimento"
    while i < len(lines):
        line = lines[i].strip()
        if not line:
            i += 1
            continue
        if "|" in line and line.lower().startswith("data"):
            i += 1
            break
        i += 1
    while i < len(lines):
        line = lines[i].strip()
        if not line:
            i += 1
            continue
        if RE_PROCESO_TJRJ.match(line) or (line.startswith("LOTE ") and "Instância" in line):
            break
        parts = [p.strip() for p in line.split("|", 1)]
        if len(parts) >= 2:
            date_raw, desc = parts[0], parts[1]
            normalized = _normalize_partial_date(date_raw)
            if normalized and desc:
                movements.append((normalized, desc))
        i += 1
    return movements, i


def build_markdown(
    processes: list[ProcessBlock],
    source_path: Path,
    niteroi_note: str | None,
    source_title: str | None = None,
) -> str:
    lines: list[str] = []
    title = source_title if source_title else "Movimentos PJE — consolidado"
    lines.append(f"# {title}")
    lines.append("")
    lines.append(f"- **Fonte:** `{source_path}`")
    lines.append(
        "- **Formato das datas:** DD/MM/AAAA (hora não informada pelo relatório; usar 00:00 em importação)."
    )
    if niteroi_note:
        lines.append(f"- **Nota (bloco Niterói):** {niteroi_note.strip()}")
    lines.append("")
    # Group by comarca
    by_comarca: dict[str, list[ProcessBlock]] = {}
    for p in processes:
        c = p.comarca or "(Comarca não identificada)"
        by_comarca.setdefault(c, []).append(p)

    for comarca in sorted(by_comarca.keys(), key=lambda x: x.lower()):
        lines.append(f"## {comarca}")
        lines.append("")
        for pb in sorted(by_comarca[comarca], key=lambda x: x.cnj):
            tipo_s = f" ({pb.tipo})" if pb.tipo else ""
            lines.append(f"### {pb.cnj}{tipo_s}")
            lines.append("")
            if pb.partes:
                lines.append(f"- **Partes:** {pb.partes}")
            if pb.vara:
                lines.append(f"- **Vara:** {pb.vara}")
            if pb.distribuido:
                lines.append(f"- **Distribuído em:** {pb.distribuido}")
            lines.append("")
            lines.append("| Data | Descrição |")
            lines.append("|------|-----------|")
            for dt, desc in pb.movements:
                desc_esc = desc.replace("|", "\\|")
                lines.append(f"| {dt} | {desc_esc} |")
            lines.append("")
    return "\n".join(lines)


def parse_lines(lines: list[str]) -> tuple[list[ProcessBlock], str | None]:
    processes: list[ProcessBlock] = []
    current_comarca = "Documento"
    niteroi_note: str | None = None
    niteroi_meta_lines: list[str] = []

    i = 0
    in_niteroi = False

    while i < len(lines):
        line = lines[i].strip()

        # Niterói report header
        if line.startswith("# Relatório de Movimentos - Comarca de Niterói"):
            in_niteroi = True
            current_comarca = "Niterói"
            i += 1
            # collect ** meta until ---
            while i < len(lines):
                ln = lines[i].strip()
                if ln.startswith("---"):
                    i += 1
                    break
                if ln.startswith("**") or ln:
                    niteroi_meta_lines.append(ln)
                i += 1
            niteroi_note = " ".join(niteroi_meta_lines)
            continue

        # Comarca section markers (emoji or RELATÓRIO)
        if re.match(r"^📍\s+", line) or line.startswith("COMARCA DE "):
            m = re.match(r"^📍\s+COMARCA DE\s+(.+)$", line, re.I)
            if m:
                current_comarca = m.group(1).strip()
            elif line.startswith("COMARCA DE "):
                current_comarca = line.replace("COMARCA DE ", "").strip()
            i += 1
            continue

        if line.startswith("RELATÓRIO") and "COMARCA" in line.upper():
            # e.g. RELATÓRIO — COMARCA DE RIO DAS OSTRAS
            m = re.search(r"COMARCA DE\s+(.+?)(?:\s*$|·)", line, re.I)
            if m:
                current_comarca = m.group(1).strip()
            i += 1
            continue

        # TJRJ "RELATÓRIO DE ANDAMENTOS PROCESSUAIS" — PROCESSO N – CNJ
        m_tjrj = RE_PROCESO_TJRJ.match(line)
        if m_tjrj:
            cnj = m_tjrj.group(1)
            i += 1
            partes = None
            if i < len(lines):
                ln = lines[i].strip()
                if "Exequente:" in ln or "Executado:" in ln:
                    partes = ln
                    i += 1
            movs, i = parse_tjrj_table_lines(lines, i)
            movs_sorted = sorted(movs, key=lambda x: sort_key_date(x[0]))
            processes.append(
                ProcessBlock(
                    comarca="TJRJ - Segunda Instância",
                    cnj=cnj,
                    tipo=None,
                    partes=partes,
                    movements=movs_sorted,
                )
            )
            continue

        m_head = RE_HEADING_NITER.match(line)
        if in_niteroi and m_head:
            tipo = m_head.group("tipo")
            cnj = m_head.group("cnj")
            i += 1
            movs, i = parse_niteroi_table_lines(lines, i)
            movs_sorted = sorted(movs, key=lambda x: sort_key_date(x[0]))
            processes.append(
                ProcessBlock(
                    comarca="Niterói",
                    cnj=cnj,
                    tipo=tipo,
                    movements=movs_sorted,
                )
            )
            continue

        m_proc = RE_PROCESSO.match(line)
        if m_proc:
            cnj = m_proc.group("cnj")
            i += 1
            partes = vara = tipo = dist = None
            blob_lines: list[str] = []
            while i < len(lines):
                ln = lines[i].strip()
                if (
                    RE_PROCESSO.match(ln)
                    or RE_HEADING_NITER.match(ln)
                    or ln.startswith("# Relatório de Movimentos")
                    or (ln.startswith("RELATÓRIO") and "COMARCA" in ln.upper())
                    or re.match(r"^📍\s+", ln)
                    or ln.startswith("COMARCA DE ")
                ):
                    break
                if ln.startswith("Partes:"):
                    partes = ln[len("Partes:") :].strip()
                elif ln.startswith("Vara:"):
                    vara = ln[len("Vara:") :].strip()
                elif ln.startswith("Tipo:"):
                    tipo = ln[len("Tipo:") :].strip()
                elif re.match(r"^Distribu[ií]do em:\s*", ln, re.I):
                    dist = re.sub(r"^Distribu[ií]do em:\s*", "", ln, flags=re.I).strip()
                elif RE_DATADESC_PREFIX.match(ln) or (
                    blob_lines
                    and not ln.startswith(("Partes:", "Vara:", "Tipo:"))
                    and not re.match(r"^Distribu[ií]do em:\s*", ln, re.I)
                ):
                    blob_lines.append(ln)
                i += 1
            blob = "".join(blob_lines)
            pairs = split_datadescricao(blob)
            pairs_sorted = sorted(pairs, key=lambda x: sort_key_date(x[0]))
            comarca = infer_comarca_from_vara(vara) or current_comarca
            processes.append(
                ProcessBlock(
                    comarca=comarca,
                    cnj=cnj,
                    tipo=tipo,
                    partes=partes,
                    vara=vara,
                    distribuido=dist,
                    movements=pairs_sorted,
                )
            )
            continue

        # Fallback: "Processo nº" without leading number
        m_alt = RE_PROCESSO_ALT.match(line)
        if m_alt and not in_niteroi:
            cnj = m_alt.group("cnj")
            i += 1
            partes = vara = tipo = dist = None
            blob_lines = []
            while i < len(lines):
                ln = lines[i].strip()
                if (
                    RE_PROCESSO.match(ln)
                    or RE_PROCESSO_ALT.match(ln)
                    or RE_HEADING_NITER.match(ln)
                    or ln.startswith("# Relatório")
                ):
                    break
                if ln.startswith("Partes:"):
                    partes = ln[len("Partes:") :].strip()
                elif ln.startswith("Vara:"):
                    vara = ln[len("Vara:") :].strip()
                elif ln.startswith("Tipo:"):
                    tipo = ln[len("Tipo:") :].strip()
                elif re.match(r"^Distribu[ií]do em:\s*", ln, re.I):
                    dist = re.sub(r"^Distribu[ií]do em:\s*", "", ln, flags=re.I).strip()
                else:
                    blob_lines.append(ln)
                i += 1
            blob = "".join(blob_lines)
            pairs = split_datadescricao(blob)
            pairs_sorted = sorted(pairs, key=lambda x: sort_key_date(x[0]))
            comarca = infer_comarca_from_vara(vara) or current_comarca
            processes.append(
                ProcessBlock(
                    comarca=comarca,
                    cnj=cnj,
                    tipo=tipo,
                    partes=partes,
                    vara=vara,
                    distribuido=dist,
                    movements=pairs_sorted,
                )
            )
            continue

        i += 1

    return processes, niteroi_note


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument(
        "docx",
        type=Path,
        nargs="?",
        default=Path(r"c:\Users\Paranhos\Downloads\movimentos PJE.docx"),
    )
    ap.add_argument(
        "-o",
        "--output",
        type=Path,
        default=Path("docs/movimentos-pje-unificado.md"),
    )
    ap.add_argument(
        "--dump-paragraphs",
        action="store_true",
        help="Print extracted paragraphs for format analysis (no MD output)",
    )
    args = ap.parse_args()
    if not args.docx.is_file():
        print(f"File not found: {args.docx}", file=sys.stderr)
        sys.exit(1)
    paras = extract_docx_paragraphs(args.docx)
    if args.dump_paragraphs:
        for i, p in enumerate(paras):
            print(f"[{i:04d}] {p}")
        return
    processes, niteroi_note = parse_lines(paras)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    source_title = None
    if processes and all(p.comarca and p.comarca.startswith("TJRJ") for p in processes):
        source_title = "Movimentos — Relatório de Andamentos TJRJ"
    md = build_markdown(processes, args.docx.resolve(), niteroi_note, source_title)
    args.output.write_text(md, encoding="utf-8")
    print(f"Wrote {args.output} ({len(processes)} processos)")


if __name__ == "__main__":
    main()
