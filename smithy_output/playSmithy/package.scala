package object playSmithy {
  type HomeControllerService[F[_]] = smithy4s.Monadic[HomeControllerServiceGen, F]
  object HomeControllerService extends smithy4s.Service.Provider[HomeControllerServiceGen, HomeControllerServiceOperation] {
    def apply[F[_]](implicit F: HomeControllerService[F]): F.type = F
    def service : smithy4s.Service[HomeControllerServiceGen, HomeControllerServiceOperation] = HomeControllerServiceGen
    val id: smithy4s.ShapeId = service.id
  }


}