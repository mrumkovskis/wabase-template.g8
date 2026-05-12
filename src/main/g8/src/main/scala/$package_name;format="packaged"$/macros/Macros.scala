package $package_name$.macros

import org.tresql.{Expr, QueryBuilder}
import org.tresql.parsing.QueryParsers
import org.tresql.ast.{Exp, StringConst, Ident}

object Macros extends org.wabase.Macros {

  def demo(b: QueryBuilder, expr: Expr): Expr = expr
  def dynamic_table(p: QueryParsers, exp: StringConst): Exp = Ident(exp.value.split("\\\\.").toList)
}
