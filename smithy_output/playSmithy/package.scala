package object playSmithy {
  type PizzaAdminService[F[_]] = smithy4s.Monadic[PizzaAdminServiceGen, F]
  object PizzaAdminService extends smithy4s.Service.Provider[PizzaAdminServiceGen, PizzaAdminServiceOperation] {
    def apply[F[_]](implicit F: PizzaAdminService[F]): F.type = F
    def service : smithy4s.Service[PizzaAdminServiceGen, PizzaAdminServiceOperation] = PizzaAdminServiceGen
    val id: smithy4s.ShapeId = service.id
  }
  type HomeControllerService[F[_]] = smithy4s.Monadic[HomeControllerServiceGen, F]
  object HomeControllerService extends smithy4s.Service.Provider[HomeControllerServiceGen, HomeControllerServiceOperation] {
    def apply[F[_]](implicit F: HomeControllerService[F]): F.type = F
    def service : smithy4s.Service[HomeControllerServiceGen, HomeControllerServiceOperation] = HomeControllerServiceGen
    val id: smithy4s.ShapeId = service.id
  }

  type PizzaList = playSmithy.PizzaList.Type

}