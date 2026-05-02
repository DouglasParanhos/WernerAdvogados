#!/usr/bin/env python3
"""
Import movement rows from docs/movimentos-pje-unificado.md into PostgreSQL (tabela moviment).

Associa cada bloco ao processo existente pelo número CNJ (process.numero). Linhas cujo
CNJ não existir em `process` resultam em INSERT com 0 linhas (sem erro).

Requer inserção direta: pip install psycopg2-binary

Exemplos:
  python scripts/import_movimentos_md_to_db.py --dry-run
  python scripts/import_movimentos_md_to_db.py --md docs/movimentos-pje-unificado.md

Produção (Railway CLI, após `railway login`):
  railway run --service Postgres python scripts/import_movimentos_md_to_db.py --md docs/movimentos-relatorio-processos.md

Se `localhost:5432` não for o Postgres do Docker (senha diferente ou outro serviço),
gere SQL e aplique no container:

  python scripts/import_movimentos_md_to_db.py --emit-sql import_movimentos.sql
  docker cp import_movimentos.sql wa-postgres:/tmp/import_movimentos.sql
  docker exec wa-postgres psql -U postgres -d wa_db -v ON_ERROR_STOP=1 -f /tmp/import_movimentos.sql
"""
from __future__ import annotations

import argparse
import os
import re
import sys
from datetime import datetime
from pathlib import Path

def _require_psycopg2():
    try:
        import psycopg2  # noqa: F401
    except ImportError:
        print("Instale a dependência: pip install psycopg2-binary", file=sys.stderr)
        sys.exit(1)
    return __import__("psycopg2")

RE_H3 = re.compile(r"^###\s+([\d.-]+)(?:\s+\(([^)]+)\))?\s*$", re.MULTILINE)
RE_ROW = re.compile(r"^\|\s*(\d{2}/\d{2}/\d{4})\s*\|\s*(.*)$")
DESC_MAX = 255


def parse_md(path: Path) -> list[tuple[str, list[tuple[str, str]]]]:
    """Return list of (cnj, [(dd/mm/yyyy, desc), ...])."""
    text = path.read_text(encoding="utf-8")
    sections: list[tuple[str, list[tuple[str, str]]]] = []
    parts = re.split(r"(?=^###\s+[\d.-])", text, flags=re.MULTILINE)
    for part in parts:
        if not part.strip():
            continue
        lines = part.strip().splitlines()
        if not lines:
            continue
        m = RE_H3.match(lines[0].strip())
        if not m:
            continue
        cnj = m.group(1).strip()
        movements: list[tuple[str, str]] = []
        for line in lines[1:]:
            line = line.rstrip()
            rm = RE_ROW.match(line)
            if not rm:
                continue
            ds, desc = rm.group(1), rm.group(2).strip()
            if desc.endswith("|"):
                desc = desc[:-1].strip()
            desc = desc.replace("\\|", "|")
            if ds.lower() == "data" or "----" in line:
                continue
            movements.append((ds, desc))
        if movements:
            sections.append((cnj, movements))
    return sections


def normalize_cnj(s: str) -> str:
    return re.sub(r"\s+", "", s.strip())


def lookup_process_id(cur, numero: str) -> int | None:
    cur.execute(
        """
        SELECT process_id FROM process
        WHERE TRIM(numero) = %s OR LOWER(TRIM(numero)) = LOWER(%s)
        LIMIT 1
        """,
        (numero, numero),
    )
    row = cur.fetchone()
    return int(row[0]) if row else None


def movement_exists(cur, process_id: int, dt, descricao: str) -> bool:
    cur.execute(
        """
        SELECT 1 FROM moviment
        WHERE process_id = %s AND date = %s AND descricao = %s
        LIMIT 1
        """,
        (process_id, dt, descricao),
    )
    return cur.fetchone() is not None


def parse_date(dmy: str) -> datetime:
    d, m, y = dmy.split("/")
    return datetime(int(y), int(m), int(d), 0, 0, 0)


def sql_escape(s: str) -> str:
    return s.replace("'", "''")


def emit_sql_file(
    sections: list[tuple[str, list[tuple[str, str]]]],
    out: Path,
) -> None:
    """Gera SQL com INSERT ... SELECT a partir de process.numero (útil quando o host não alcança o Postgres do Docker)."""
    lines = [
        "-- Gerado por import_movimentos_md_to_db.py --emit-sql",
        "-- Executar no banco wa_db, ex.: docker exec -i wa-postgres psql -U postgres -d wa_db -f - < este_arquivo.sql",
        "BEGIN;",
    ]
    n = 0
    for cnj, movements in sections:
        cnj_sql = sql_escape(normalize_cnj(cnj))
        for dmy, desc in movements:
            if len(desc) > DESC_MAX:
                desc = desc[:DESC_MAX]
            d_esc = sql_escape(desc)
            dmy_esc = sql_escape(dmy)
            lines.append(
                f"""INSERT INTO moviment (descricao, date, process_id, visible_to_client, created_on, modified_on)
SELECT '{d_esc}', (to_timestamp('{dmy_esc}', 'DD/MM/YYYY'))::timestamp, p.process_id, true, NOW(), NOW()
FROM process p
WHERE LOWER(TRIM(p.numero)) = LOWER('{cnj_sql}')
AND NOT EXISTS (
  SELECT 1 FROM moviment m
  WHERE m.process_id = p.process_id
    AND m.date = (to_timestamp('{dmy_esc}', 'DD/MM/YYYY'))::timestamp
    AND m.descricao = '{d_esc}'
)
LIMIT 1;"""
            )
            n += 1
    lines.append("COMMIT;")
    out.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Escrito {out} ({n} comandos INSERT).")


def main() -> None:
    ap = argparse.ArgumentParser(description="Importa movimentos do MD para PostgreSQL")
    ap.add_argument(
        "--md",
        type=Path,
        default=Path("docs/movimentos-pje-unificado.md"),
        help="Arquivo Markdown unificado",
    )
    ap.add_argument("--host", default="localhost")
    ap.add_argument("--port", type=int, default=5432)
    ap.add_argument("--dbname", default="wa_db")
    ap.add_argument("--user", default="postgres")
    ap.add_argument("--password", default="postgres")
    ap.add_argument(
        "--database-url",
        default=os.environ.get("DATABASE_URL"),
        help="URL de conexão (ex: postgresql://user:pass@host:port/db). Railway: railway run --service Postgres python ...",
    )
    ap.add_argument(
        "--dry-run",
        action="store_true",
        help="Só mostra o que seria inserido",
    )
    ap.add_argument(
        "--skip-duplicates",
        action="store_true",
        default=True,
        help="Não insere se já existir mesmo process_id+date+descricao (default: on)",
    )
    ap.add_argument(
        "--emit-sql",
        type=Path,
        metavar="FILE.sql",
        help="Só gera arquivo SQL (sem psycopg2 no host); aplicar com psql no container Docker",
    )
    args = ap.parse_args()

    if not args.md.is_file():
        print(f"Arquivo não encontrado: {args.md}", file=sys.stderr)
        sys.exit(1)

    sections = parse_md(args.md)
    total_movs = sum(len(m) for _, m in sections)
    print(f"Lidos {len(sections)} processos com movimentos, {total_movs} linhas no MD.")

    if args.emit_sql:
        emit_sql_file(sections, args.emit_sql)
        return

    if args.dry_run:
        print("[dry-run] Não conectando ao banco. Primeiros 3 blocos:")
        for cnj, movs in sections[:3]:
            print(f"  {cnj}: {len(movs)} movimentos")
        return

    conn_kw: dict
    if args.database_url:
        conn_kw = {"dsn": args.database_url}
    else:
        conn_kw = dict(
            host=args.host,
            port=args.port,
            dbname=args.dbname,
            user=args.user,
            password=args.password,
        )

    psycopg2 = _require_psycopg2()
    conn = psycopg2.connect(**conn_kw)
    try:
        cur = conn.cursor()
        inserted = 0
        skipped_dup = 0
        skipped_no_process = 0
        truncated = 0

        for cnj, movements in sections:
            cnj_n = normalize_cnj(cnj)
            pid = lookup_process_id(cur, cnj_n)
            if pid is None:
                pid = lookup_process_id(cur, cnj)
            if pid is None:
                skipped_no_process += 1
                print(f"[aviso] Processo não cadastrado no banco, ignorado: {cnj}")
                continue

            for dmy, desc in movements:
                if len(desc) > DESC_MAX:
                    desc = desc[:DESC_MAX]
                    truncated += 1
                dt = parse_date(dmy)
                if args.skip_duplicates and movement_exists(cur, pid, dt, desc):
                    skipped_dup += 1
                    continue
                cur.execute(
                    """
                    INSERT INTO moviment (descricao, date, process_id, visible_to_client, created_on, modified_on)
                    VALUES (%s, %s, %s, true, NOW(), NOW())
                    """,
                    (desc, dt, pid),
                )
                inserted += 1

        conn.commit()
        print(
            f"Inseridos: {inserted}; duplicados ignorados: {skipped_dup}; "
            f"processos CNJ não encontrados: {skipped_no_process}; descrições truncadas a {DESC_MAX}: {truncated}"
        )
    finally:
        conn.close()


if __name__ == "__main__":
    main()
