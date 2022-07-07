namespace playSmithy

use smithy4s.api#simpleRestJson

@simpleRestJson
service PizzaAdminService {
    version: "1.0.0",
    errors: [GenericServerError, GenericClientError],
    operations: [AddMenuItem, GetMenu, Version, Health]
}

@http(method: "POST", uri: "/menu/item", code: 201)
operation AddMenuItem {
    input: AddMenuItemRequest,
    errors: [PriceError],
    output: MenuItem
}


@readonly
@http(method: "POST", uri: "/version", code: 200)
operation Version {
    input: VersionInput,
    output: VersionOutput
}

structure VersionInput {
    @httpPayload
    @required
    body: Blob
}

structure VersionOutput {
    @httpPayload
    @required
    version: Document
}

@error("client")
structure PriceError {
    @required
    message: String
}

@readonly
@http(method: "GET", uri: "/item/{id}", code: 200)
operation GetMenu {
    input: GetMenuRequest,
    errors: [NotFoundError, FallbackError],
    output: GetMenuResult
}

structure GetMenuRequest {
    @httpLabel
    @required
    id: String
}

structure GetMenuResult {
    @required
    @httpPayload
    item: MenuItem
}

@error("client")
@httpError(404)
structure NotFoundError {
    @required
    name: String
}

@error("client")
structure FallbackError {
    @required
    error: String
}


structure AddMenuItemRequest {
    @httpPayload
    @required
    menuItem: MenuItem
}

structure MenuItem {
    @required
    food: Pizza,
    @required
    price: Float,
    id: String,
    added: String
}

structure Pizza {
    @required
    name: String,
    @required
    base: String,
    @required
    toppings: String
}

@error("server")
@httpError(502)
structure GenericServerError {
    @required
    message: String
}

@error("client")
@httpError(418)
structure GenericClientError {
    @required
    message: String
}

@readonly
@http(method: "GET", uri: "/health", code: 200)
operation Health {
    input: HealthRequest,
    output: HealthResponse,
    errors: [ UnknownServerError ]
}

structure HealthRequest {
    @httpQuery("query")
    @length(min: 0, max: 5)
    query: String
}

structure HealthResponse {
    @required
    status: String
}

// This error indicates a fatal, unexpected error has occurred. Fallback strategies ought to be triggered by this
// error.
@error("server")
@httpError(500)
structure UnknownServerError {
    @required
    errorCode: UnknownServerErrorCode,

    description: String,

    stateHash: String
}

// Define the singular error code that can be returned for an UnknownServerError
@enum([
    {
        value: "server.error",
        name: "ERROR_CODE"
    }
])
string UnknownServerErrorCode


