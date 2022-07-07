package playSmithy

import PizzaAdminServiceGen.AddMenuItemError
import PizzaAdminServiceGen.GetMenuError
import PizzaAdminServiceGen.HealthError
import PizzaAdminServiceGen.VersionError
import smithy4s.ByteArray
import smithy4s.schema.Schema._

trait PizzaAdminServiceGen[F[_, _, _, _, _]] {
  self =>

  def addMenuItem(menuItem: MenuItem) : F[AddMenuItemRequest, AddMenuItemError, MenuItem, Nothing, Nothing]
  def getMenu(id: String) : F[GetMenuRequest, GetMenuError, GetMenuResult, Nothing, Nothing]
  def version(body: ByteArray) : F[VersionInput, VersionError, VersionOutput, Nothing, Nothing]
  def health(query: Option[String] = None) : F[HealthRequest, HealthError, HealthResponse, Nothing, Nothing]

  def transform[G[_, _, _, _, _]](transformation : smithy4s.Transformation[F, G]) : PizzaAdminServiceGen[G] = new Transformed(transformation)
  class Transformed[G[_, _, _, _, _]](transformation : smithy4s.Transformation[F, G]) extends PizzaAdminServiceGen[G] {
    def addMenuItem(menuItem: MenuItem) = transformation[AddMenuItemRequest, AddMenuItemError, MenuItem, Nothing, Nothing](self.addMenuItem(menuItem))
    def getMenu(id: String) = transformation[GetMenuRequest, GetMenuError, GetMenuResult, Nothing, Nothing](self.getMenu(id))
    def version(body: ByteArray) = transformation[VersionInput, VersionError, VersionOutput, Nothing, Nothing](self.version(body))
    def health(query: Option[String] = None) = transformation[HealthRequest, HealthError, HealthResponse, Nothing, Nothing](self.health(query))
  }
}

object PizzaAdminServiceGen extends smithy4s.Service[PizzaAdminServiceGen, PizzaAdminServiceOperation] {

  def apply[F[_]](implicit F: smithy4s.Monadic[PizzaAdminServiceGen, F]): F.type = F

  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "PizzaAdminService")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy4s.api.SimpleRestJson(),
  )

  val endpoints = List(
    AddMenuItem,
    GetMenu,
    Version,
    Health,
  )

  val version: String = "1.0.0"

  def endpoint[I, E, O, SI, SO](op : PizzaAdminServiceOperation[I, E, O, SI, SO]) = op match {
    case AddMenuItem(input) => (input, AddMenuItem)
    case GetMenu(input) => (input, GetMenu)
    case Version(input) => (input, Version)
    case Health(input) => (input, Health)
  }

  object reified extends PizzaAdminServiceGen[PizzaAdminServiceOperation] {
    def addMenuItem(menuItem: MenuItem) = AddMenuItem(AddMenuItemRequest(menuItem))
    def getMenu(id: String) = GetMenu(GetMenuRequest(id))
    def version(body: ByteArray) = Version(VersionInput(body))
    def health(query: Option[String] = None) = Health(HealthRequest(query))
  }

  def transform[P[_, _, _, _, _]](transformation: smithy4s.Transformation[PizzaAdminServiceOperation, P]): PizzaAdminServiceGen[P] = reified.transform(transformation)

  def transform[P[_, _, _, _, _], P1[_, _, _, _, _]](alg: PizzaAdminServiceGen[P], transformation: smithy4s.Transformation[P, P1]): PizzaAdminServiceGen[P1] = alg.transform(transformation)

  def asTransformation[P[_, _, _, _, _]](impl : PizzaAdminServiceGen[P]): smithy4s.Transformation[PizzaAdminServiceOperation, P] = new smithy4s.Transformation[PizzaAdminServiceOperation, P] {
    def apply[I, E, O, SI, SO](op : PizzaAdminServiceOperation[I, E, O, SI, SO]) : P[I, E, O, SI, SO] = op match  {
      case AddMenuItem(AddMenuItemRequest(menuItem)) => impl.addMenuItem(menuItem)
      case GetMenu(GetMenuRequest(id)) => impl.getMenu(id)
      case Version(VersionInput(body)) => impl.version(body)
      case Health(HealthRequest(query)) => impl.health(query)
    }
  }
  case class AddMenuItem(input: AddMenuItemRequest) extends PizzaAdminServiceOperation[AddMenuItemRequest, AddMenuItemError, MenuItem, Nothing, Nothing]
  object AddMenuItem extends smithy4s.Endpoint[PizzaAdminServiceOperation, AddMenuItemRequest, AddMenuItemError, MenuItem, Nothing, Nothing] with smithy4s.Errorable[AddMenuItemError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "AddMenuItem")
    val input: smithy4s.Schema[AddMenuItemRequest] = AddMenuItemRequest.schema.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[MenuItem] = MenuItem.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("POST"), smithy.api.NonEmptyString("/menu/item"), Some(201)),
    )
    def wrap(input: AddMenuItemRequest) = AddMenuItem(input)
    override val errorable: Option[smithy4s.Errorable[AddMenuItemError]] = Some(this)
    val error: smithy4s.UnionSchema[AddMenuItemError] = AddMenuItemError.schema
    def liftError(throwable: Throwable) : Option[AddMenuItemError] = throwable match {
      case e: GenericServerError => Some(AddMenuItemError.GenericServerErrorCase(e))
      case e: GenericClientError => Some(AddMenuItemError.GenericClientErrorCase(e))
      case e: PriceError => Some(AddMenuItemError.PriceErrorCase(e))
      case _ => None
    }
    def unliftError(e: AddMenuItemError) : Throwable = e match {
      case AddMenuItemError.GenericServerErrorCase(e) => e
      case AddMenuItemError.GenericClientErrorCase(e) => e
      case AddMenuItemError.PriceErrorCase(e) => e
    }
  }
  sealed trait AddMenuItemError extends scala.Product with scala.Serializable
  object AddMenuItemError extends smithy4s.ShapeTag.Companion[AddMenuItemError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "AddMenuItemError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class GenericServerErrorCase(genericServerError: GenericServerError) extends AddMenuItemError
    case class GenericClientErrorCase(genericClientError: GenericClientError) extends AddMenuItemError
    case class PriceErrorCase(priceError: PriceError) extends AddMenuItemError

    object GenericServerErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[GenericServerErrorCase] = bijection(GenericServerError.schema.addHints(hints), GenericServerErrorCase(_), _.genericServerError)
      val alt = schema.oneOf[AddMenuItemError]("GenericServerError")
    }
    object GenericClientErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[GenericClientErrorCase] = bijection(GenericClientError.schema.addHints(hints), GenericClientErrorCase(_), _.genericClientError)
      val alt = schema.oneOf[AddMenuItemError]("GenericClientError")
    }
    object PriceErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[PriceErrorCase] = bijection(PriceError.schema.addHints(hints), PriceErrorCase(_), _.priceError)
      val alt = schema.oneOf[AddMenuItemError]("PriceError")
    }

    implicit val schema: smithy4s.UnionSchema[AddMenuItemError] = union(
      GenericServerErrorCase.alt,
      GenericClientErrorCase.alt,
      PriceErrorCase.alt,
    ){
      case c : GenericServerErrorCase => GenericServerErrorCase.alt(c)
      case c : GenericClientErrorCase => GenericClientErrorCase.alt(c)
      case c : PriceErrorCase => PriceErrorCase.alt(c)
    }
  }
  case class GetMenu(input: GetMenuRequest) extends PizzaAdminServiceOperation[GetMenuRequest, GetMenuError, GetMenuResult, Nothing, Nothing]
  object GetMenu extends smithy4s.Endpoint[PizzaAdminServiceOperation, GetMenuRequest, GetMenuError, GetMenuResult, Nothing, Nothing] with smithy4s.Errorable[GetMenuError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "GetMenu")
    val input: smithy4s.Schema[GetMenuRequest] = GetMenuRequest.schema.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[GetMenuResult] = GetMenuResult.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("GET"), smithy.api.NonEmptyString("/item/{id}"), Some(200)),
      smithy.api.Readonly(),
    )
    def wrap(input: GetMenuRequest) = GetMenu(input)
    override val errorable: Option[smithy4s.Errorable[GetMenuError]] = Some(this)
    val error: smithy4s.UnionSchema[GetMenuError] = GetMenuError.schema
    def liftError(throwable: Throwable) : Option[GetMenuError] = throwable match {
      case e: GenericServerError => Some(GetMenuError.GenericServerErrorCase(e))
      case e: GenericClientError => Some(GetMenuError.GenericClientErrorCase(e))
      case e: NotFoundError => Some(GetMenuError.NotFoundErrorCase(e))
      case e: FallbackError => Some(GetMenuError.FallbackErrorCase(e))
      case _ => None
    }
    def unliftError(e: GetMenuError) : Throwable = e match {
      case GetMenuError.GenericServerErrorCase(e) => e
      case GetMenuError.GenericClientErrorCase(e) => e
      case GetMenuError.NotFoundErrorCase(e) => e
      case GetMenuError.FallbackErrorCase(e) => e
    }
  }
  sealed trait GetMenuError extends scala.Product with scala.Serializable
  object GetMenuError extends smithy4s.ShapeTag.Companion[GetMenuError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "GetMenuError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class GenericServerErrorCase(genericServerError: GenericServerError) extends GetMenuError
    case class GenericClientErrorCase(genericClientError: GenericClientError) extends GetMenuError
    case class NotFoundErrorCase(notFoundError: NotFoundError) extends GetMenuError
    case class FallbackErrorCase(fallbackError: FallbackError) extends GetMenuError

    object GenericServerErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[GenericServerErrorCase] = bijection(GenericServerError.schema.addHints(hints), GenericServerErrorCase(_), _.genericServerError)
      val alt = schema.oneOf[GetMenuError]("GenericServerError")
    }
    object GenericClientErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[GenericClientErrorCase] = bijection(GenericClientError.schema.addHints(hints), GenericClientErrorCase(_), _.genericClientError)
      val alt = schema.oneOf[GetMenuError]("GenericClientError")
    }
    object NotFoundErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[NotFoundErrorCase] = bijection(NotFoundError.schema.addHints(hints), NotFoundErrorCase(_), _.notFoundError)
      val alt = schema.oneOf[GetMenuError]("NotFoundError")
    }
    object FallbackErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[FallbackErrorCase] = bijection(FallbackError.schema.addHints(hints), FallbackErrorCase(_), _.fallbackError)
      val alt = schema.oneOf[GetMenuError]("FallbackError")
    }

    implicit val schema: smithy4s.UnionSchema[GetMenuError] = union(
      GenericServerErrorCase.alt,
      GenericClientErrorCase.alt,
      NotFoundErrorCase.alt,
      FallbackErrorCase.alt,
    ){
      case c : GenericServerErrorCase => GenericServerErrorCase.alt(c)
      case c : GenericClientErrorCase => GenericClientErrorCase.alt(c)
      case c : NotFoundErrorCase => NotFoundErrorCase.alt(c)
      case c : FallbackErrorCase => FallbackErrorCase.alt(c)
    }
  }
  case class Version(input: VersionInput) extends PizzaAdminServiceOperation[VersionInput, VersionError, VersionOutput, Nothing, Nothing]
  object Version extends smithy4s.Endpoint[PizzaAdminServiceOperation, VersionInput, VersionError, VersionOutput, Nothing, Nothing] with smithy4s.Errorable[VersionError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "Version")
    val input: smithy4s.Schema[VersionInput] = VersionInput.schema.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[VersionOutput] = VersionOutput.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("POST"), smithy.api.NonEmptyString("/version"), Some(200)),
      smithy.api.Readonly(),
    )
    def wrap(input: VersionInput) = Version(input)
    override val errorable: Option[smithy4s.Errorable[VersionError]] = Some(this)
    val error: smithy4s.UnionSchema[VersionError] = VersionError.schema
    def liftError(throwable: Throwable) : Option[VersionError] = throwable match {
      case e: GenericServerError => Some(VersionError.GenericServerErrorCase(e))
      case e: GenericClientError => Some(VersionError.GenericClientErrorCase(e))
      case _ => None
    }
    def unliftError(e: VersionError) : Throwable = e match {
      case VersionError.GenericServerErrorCase(e) => e
      case VersionError.GenericClientErrorCase(e) => e
    }
  }
  sealed trait VersionError extends scala.Product with scala.Serializable
  object VersionError extends smithy4s.ShapeTag.Companion[VersionError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "VersionError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class GenericServerErrorCase(genericServerError: GenericServerError) extends VersionError
    case class GenericClientErrorCase(genericClientError: GenericClientError) extends VersionError

    object GenericServerErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[GenericServerErrorCase] = bijection(GenericServerError.schema.addHints(hints), GenericServerErrorCase(_), _.genericServerError)
      val alt = schema.oneOf[VersionError]("GenericServerError")
    }
    object GenericClientErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[GenericClientErrorCase] = bijection(GenericClientError.schema.addHints(hints), GenericClientErrorCase(_), _.genericClientError)
      val alt = schema.oneOf[VersionError]("GenericClientError")
    }

    implicit val schema: smithy4s.UnionSchema[VersionError] = union(
      GenericServerErrorCase.alt,
      GenericClientErrorCase.alt,
    ){
      case c : GenericServerErrorCase => GenericServerErrorCase.alt(c)
      case c : GenericClientErrorCase => GenericClientErrorCase.alt(c)
    }
  }
  case class Health(input: HealthRequest) extends PizzaAdminServiceOperation[HealthRequest, HealthError, HealthResponse, Nothing, Nothing]
  object Health extends smithy4s.Endpoint[PizzaAdminServiceOperation, HealthRequest, HealthError, HealthResponse, Nothing, Nothing] with smithy4s.Errorable[HealthError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "Health")
    val input: smithy4s.Schema[HealthRequest] = HealthRequest.schema.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[HealthResponse] = HealthResponse.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("GET"), smithy.api.NonEmptyString("/health"), Some(200)),
      smithy.api.Readonly(),
    )
    def wrap(input: HealthRequest) = Health(input)
    override val errorable: Option[smithy4s.Errorable[HealthError]] = Some(this)
    val error: smithy4s.UnionSchema[HealthError] = HealthError.schema
    def liftError(throwable: Throwable) : Option[HealthError] = throwable match {
      case e: GenericServerError => Some(HealthError.GenericServerErrorCase(e))
      case e: GenericClientError => Some(HealthError.GenericClientErrorCase(e))
      case e: UnknownServerError => Some(HealthError.UnknownServerErrorCase(e))
      case _ => None
    }
    def unliftError(e: HealthError) : Throwable = e match {
      case HealthError.GenericServerErrorCase(e) => e
      case HealthError.GenericClientErrorCase(e) => e
      case HealthError.UnknownServerErrorCase(e) => e
    }
  }
  sealed trait HealthError extends scala.Product with scala.Serializable
  object HealthError extends smithy4s.ShapeTag.Companion[HealthError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "HealthError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class GenericServerErrorCase(genericServerError: GenericServerError) extends HealthError
    case class GenericClientErrorCase(genericClientError: GenericClientError) extends HealthError
    case class UnknownServerErrorCase(unknownServerError: UnknownServerError) extends HealthError

    object GenericServerErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[GenericServerErrorCase] = bijection(GenericServerError.schema.addHints(hints), GenericServerErrorCase(_), _.genericServerError)
      val alt = schema.oneOf[HealthError]("GenericServerError")
    }
    object GenericClientErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[GenericClientErrorCase] = bijection(GenericClientError.schema.addHints(hints), GenericClientErrorCase(_), _.genericClientError)
      val alt = schema.oneOf[HealthError]("GenericClientError")
    }
    object UnknownServerErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[UnknownServerErrorCase] = bijection(UnknownServerError.schema.addHints(hints), UnknownServerErrorCase(_), _.unknownServerError)
      val alt = schema.oneOf[HealthError]("UnknownServerError")
    }

    implicit val schema: smithy4s.UnionSchema[HealthError] = union(
      GenericServerErrorCase.alt,
      GenericClientErrorCase.alt,
      UnknownServerErrorCase.alt,
    ){
      case c : GenericServerErrorCase => GenericServerErrorCase.alt(c)
      case c : GenericClientErrorCase => GenericClientErrorCase.alt(c)
      case c : UnknownServerErrorCase => UnknownServerErrorCase.alt(c)
    }
  }
}

sealed trait PizzaAdminServiceOperation[Input, Err, Output, StreamedInput, StreamedOutput]
