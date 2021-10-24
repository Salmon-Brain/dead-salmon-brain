package ai.salmon.computing

trait BaseStat {
  def square[T](x: T)(implicit num: Numeric[T]): T = {
    import num._
    x * x
  }

  def count[A](a: Seq[A])(implicit num: Fractional[A]): A =
    a.foldLeft(num.zero) { case (cnt, _) => num.plus(cnt, num.one) }

  def mean[A](a: Seq[A])(implicit num: Fractional[A]): A = num.div(a.sum, count(a))

  def variance[A](a: Seq[A])(implicit num: Fractional[A]): A =
    num.div(a.map(xs => square(num.minus(xs, mean(a)))).sum, num.minus(count(a), num.one))
}
