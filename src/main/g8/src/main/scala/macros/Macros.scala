package $package_name$.macros

import org.tresql.{Expr, QueryBuilder}

class Macros extends org.wabase.Macros {

  def demo(b: QueryBuilder, expr: Expr): Expr = expr

}
