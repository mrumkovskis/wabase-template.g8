package $package_name$.macros

import org.tresql.{Expr, QueryBuilder}
import org.tresql.parsing.QueryParsers
import org.tresql.ast.{Exp, StringConst, Ident}

object Macros extends org.wabase.Macros {

  def demo(b: QueryBuilder, expr: Expr): Expr = expr
  // beware of parser macro written in scala, because for view compilation it would be compiled with project scala version but views are compiled with scala 2.12
  // in this case you should configure macro sources in project/build.sbt in order to compile them with scala 2.12,
  // MojozMacroCompile config in build.sbt should be removed.
  //def dynamic_table(p: QueryParsers, exp: StringConst): Exp = Ident(exp.value.split("\\\\.").toList)
}
