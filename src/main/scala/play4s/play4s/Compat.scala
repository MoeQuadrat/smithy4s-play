package smithy4s.play4s

object Compat {
    type EffectCompat[F[_]] = cats.effect.Concurrent[F]
    val EffectCompat = cats.effect.Concurrent
}
