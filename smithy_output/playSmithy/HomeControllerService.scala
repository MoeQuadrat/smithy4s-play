package playSmithy

import HomeControllerServiceGen.Index1Error
import HomeControllerServiceGen.Index2Error
import HomeControllerServiceGen.IndexError
import HomeControllerServiceGen.IndexPostError
import smithy4s.schema.Schema._

trait HomeControllerServiceGen[F[_, _, _, _, _]] {
  self =>

  def index() : F[Unit, IndexError, Hi, Nothing, Nothing]
  def index1() : F[Unit, Index1Error, Hi, Nothing, Nothing]
  def index2() : F[Unit, Index2Error, Hi, Nothing, Nothing]
  def indexPost(message: Option[String] = None) : F[Bye, IndexPostError, Hi, Nothing, Nothing]

  def transform[G[_, _, _, _, _]](transformation : smithy4s.Transformation[F, G]) : HomeControllerServiceGen[G] = new Transformed(transformation)
  class Transformed[G[_, _, _, _, _]](transformation : smithy4s.Transformation[F, G]) extends HomeControllerServiceGen[G] {
    def index() = transformation[Unit, IndexError, Hi, Nothing, Nothing](self.index())
    def index1() = transformation[Unit, Index1Error, Hi, Nothing, Nothing](self.index1())
    def index2() = transformation[Unit, Index2Error, Hi, Nothing, Nothing](self.index2())
    def indexPost(message: Option[String] = None) = transformation[Bye, IndexPostError, Hi, Nothing, Nothing](self.indexPost(message))
  }
}

object HomeControllerServiceGen extends smithy4s.Service[HomeControllerServiceGen, HomeControllerServiceOperation] {

  def apply[F[_]](implicit F: smithy4s.Monadic[HomeControllerServiceGen, F]): F.type = F

  val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "HomeControllerService")

  val hints : smithy4s.Hints = smithy4s.Hints(
    smithy4s.api.SimpleRestJson(),
  )

  val endpoints = List(
    Index,
    Index1,
    Index2,
    IndexPost,
  )

  val version: String = "0.0.1"

  def endpoint[I, E, O, SI, SO](op : HomeControllerServiceOperation[I, E, O, SI, SO]) = op match {
    case Index() => ((), Index)
    case Index1() => ((), Index1)
    case Index2() => ((), Index2)
    case IndexPost(input) => (input, IndexPost)
  }

  object reified extends HomeControllerServiceGen[HomeControllerServiceOperation] {
    def index() = Index()
    def index1() = Index1()
    def index2() = Index2()
    def indexPost(message: Option[String] = None) = IndexPost(Bye(message))
  }

  def transform[P[_, _, _, _, _]](transformation: smithy4s.Transformation[HomeControllerServiceOperation, P]): HomeControllerServiceGen[P] = reified.transform(transformation)

  def transform[P[_, _, _, _, _], P1[_, _, _, _, _]](alg: HomeControllerServiceGen[P], transformation: smithy4s.Transformation[P, P1]): HomeControllerServiceGen[P1] = alg.transform(transformation)

  def asTransformation[P[_, _, _, _, _]](impl : HomeControllerServiceGen[P]): smithy4s.Transformation[HomeControllerServiceOperation, P] = new smithy4s.Transformation[HomeControllerServiceOperation, P] {
    def apply[I, E, O, SI, SO](op : HomeControllerServiceOperation[I, E, O, SI, SO]) : P[I, E, O, SI, SO] = op match  {
      case Index() => impl.index()
      case Index1() => impl.index1()
      case Index2() => impl.index2()
      case IndexPost(Bye(message)) => impl.indexPost(message)
    }
  }
  case class Index() extends HomeControllerServiceOperation[Unit, IndexError, Hi, Nothing, Nothing]
  object Index extends smithy4s.Endpoint[HomeControllerServiceOperation, Unit, IndexError, Hi, Nothing, Nothing] with smithy4s.Errorable[IndexError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "Index")
    val input: smithy4s.Schema[Unit] = unit.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[Hi] = Hi.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("GET"), smithy.api.NonEmptyString("/index"), None),
      smithy.api.Readonly(),
    )
    def wrap(input: Unit) = Index()
    override val errorable: Option[smithy4s.Errorable[IndexError]] = Some(this)
    val error: smithy4s.UnionSchema[IndexError] = IndexError.schema
    def liftError(throwable: Throwable) : Option[IndexError] = throwable match {
      case e: GeneralServerError => Some(IndexError.GeneralServerErrorCase(e))
      case _ => None
    }
    def unliftError(e: IndexError) : Throwable = e match {
      case IndexError.GeneralServerErrorCase(e) => e
    }
  }
  sealed trait IndexError extends scala.Product with scala.Serializable
  object IndexError extends smithy4s.ShapeTag.Companion[IndexError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "IndexError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class GeneralServerErrorCase(generalServerError: GeneralServerError) extends IndexError

    object GeneralServerErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[GeneralServerErrorCase] = bijection(GeneralServerError.schema.addHints(hints), GeneralServerErrorCase(_), _.generalServerError)
      val alt = schema.oneOf[IndexError]("GeneralServerError")
    }

    implicit val schema: smithy4s.UnionSchema[IndexError] = union(
      GeneralServerErrorCase.alt,
    ){
      case c : GeneralServerErrorCase => GeneralServerErrorCase.alt(c)
    }
  }
  case class Index1() extends HomeControllerServiceOperation[Unit, Index1Error, Hi, Nothing, Nothing]
  object Index1 extends smithy4s.Endpoint[HomeControllerServiceOperation, Unit, Index1Error, Hi, Nothing, Nothing] with smithy4s.Errorable[Index1Error] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "Index1")
    val input: smithy4s.Schema[Unit] = unit.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[Hi] = Hi.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("GET"), smithy.api.NonEmptyString("/index/index"), None),
      smithy.api.Readonly(),
    )
    def wrap(input: Unit) = Index1()
    override val errorable: Option[smithy4s.Errorable[Index1Error]] = Some(this)
    val error: smithy4s.UnionSchema[Index1Error] = Index1Error.schema
    def liftError(throwable: Throwable) : Option[Index1Error] = throwable match {
      case e: GeneralServerError => Some(Index1Error.GeneralServerErrorCase(e))
      case _ => None
    }
    def unliftError(e: Index1Error) : Throwable = e match {
      case Index1Error.GeneralServerErrorCase(e) => e
    }
  }
  sealed trait Index1Error extends scala.Product with scala.Serializable
  object Index1Error extends smithy4s.ShapeTag.Companion[Index1Error] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "Index1Error")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class GeneralServerErrorCase(generalServerError: GeneralServerError) extends Index1Error

    object GeneralServerErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[GeneralServerErrorCase] = bijection(GeneralServerError.schema.addHints(hints), GeneralServerErrorCase(_), _.generalServerError)
      val alt = schema.oneOf[Index1Error]("GeneralServerError")
    }

    implicit val schema: smithy4s.UnionSchema[Index1Error] = union(
      GeneralServerErrorCase.alt,
    ){
      case c : GeneralServerErrorCase => GeneralServerErrorCase.alt(c)
    }
  }
  case class Index2() extends HomeControllerServiceOperation[Unit, Index2Error, Hi, Nothing, Nothing]
  object Index2 extends smithy4s.Endpoint[HomeControllerServiceOperation, Unit, Index2Error, Hi, Nothing, Nothing] with smithy4s.Errorable[Index2Error] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "Index2")
    val input: smithy4s.Schema[Unit] = unit.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[Hi] = Hi.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("GET"), smithy.api.NonEmptyString("/index2"), None),
      smithy.api.Readonly(),
    )
    def wrap(input: Unit) = Index2()
    override val errorable: Option[smithy4s.Errorable[Index2Error]] = Some(this)
    val error: smithy4s.UnionSchema[Index2Error] = Index2Error.schema
    def liftError(throwable: Throwable) : Option[Index2Error] = throwable match {
      case e: GeneralServerError => Some(Index2Error.GeneralServerErrorCase(e))
      case _ => None
    }
    def unliftError(e: Index2Error) : Throwable = e match {
      case Index2Error.GeneralServerErrorCase(e) => e
    }
  }
  sealed trait Index2Error extends scala.Product with scala.Serializable
  object Index2Error extends smithy4s.ShapeTag.Companion[Index2Error] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "Index2Error")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class GeneralServerErrorCase(generalServerError: GeneralServerError) extends Index2Error

    object GeneralServerErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[GeneralServerErrorCase] = bijection(GeneralServerError.schema.addHints(hints), GeneralServerErrorCase(_), _.generalServerError)
      val alt = schema.oneOf[Index2Error]("GeneralServerError")
    }

    implicit val schema: smithy4s.UnionSchema[Index2Error] = union(
      GeneralServerErrorCase.alt,
    ){
      case c : GeneralServerErrorCase => GeneralServerErrorCase.alt(c)
    }
  }
  case class IndexPost(input: Bye) extends HomeControllerServiceOperation[Bye, IndexPostError, Hi, Nothing, Nothing]
  object IndexPost extends smithy4s.Endpoint[HomeControllerServiceOperation, Bye, IndexPostError, Hi, Nothing, Nothing] with smithy4s.Errorable[IndexPostError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "IndexPost")
    val input: smithy4s.Schema[Bye] = Bye.schema.addHints(smithy4s.internals.InputOutput.Input)
    val output: smithy4s.Schema[Hi] = Hi.schema.addHints(smithy4s.internals.InputOutput.Output)
    val streamedInput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val streamedOutput : smithy4s.StreamingSchema[Nothing] = smithy4s.StreamingSchema.nothing
    val hints : smithy4s.Hints = smithy4s.Hints(
      smithy.api.Http(smithy.api.NonEmptyString("POST"), smithy.api.NonEmptyString("/index"), None),
      smithy.api.Readonly(),
    )
    def wrap(input: Bye) = IndexPost(input)
    override val errorable: Option[smithy4s.Errorable[IndexPostError]] = Some(this)
    val error: smithy4s.UnionSchema[IndexPostError] = IndexPostError.schema
    def liftError(throwable: Throwable) : Option[IndexPostError] = throwable match {
      case e: GeneralServerError => Some(IndexPostError.GeneralServerErrorCase(e))
      case _ => None
    }
    def unliftError(e: IndexPostError) : Throwable = e match {
      case IndexPostError.GeneralServerErrorCase(e) => e
    }
  }
  sealed trait IndexPostError extends scala.Product with scala.Serializable
  object IndexPostError extends smithy4s.ShapeTag.Companion[IndexPostError] {
    val id: smithy4s.ShapeId = smithy4s.ShapeId("playSmithy", "IndexPostError")

    val hints : smithy4s.Hints = smithy4s.Hints.empty

    case class GeneralServerErrorCase(generalServerError: GeneralServerError) extends IndexPostError

    object GeneralServerErrorCase {
      val hints : smithy4s.Hints = smithy4s.Hints.empty
      val schema: smithy4s.Schema[GeneralServerErrorCase] = bijection(GeneralServerError.schema.addHints(hints), GeneralServerErrorCase(_), _.generalServerError)
      val alt = schema.oneOf[IndexPostError]("GeneralServerError")
    }

    implicit val schema: smithy4s.UnionSchema[IndexPostError] = union(
      GeneralServerErrorCase.alt,
    ){
      case c : GeneralServerErrorCase => GeneralServerErrorCase.alt(c)
    }
  }
}

sealed trait HomeControllerServiceOperation[Input, Err, Output, StreamedInput, StreamedOutput]
