namespace playSmithy

use smithy4s.api#simpleRestJson


@simpleRestJson
service HomeControllerService {
    version: "0.0.1",
    errors: [GeneralServerError],
    operations: [Index, Index1, Index2]
}

@readonly
@http(method: "GET", uri: "/index")
operation Index {
    output: Hi
}

@readonly
@http(method: "GET", uri: "/index/index")
operation Index1 {
    output: Hi
}


@readonly
@http(method: "GET", uri: "/index2")
operation Index2 {
    output: Hi
}

structure Hi {
    message: String
}


@error("server")
@httpError(500)
structure GeneralServerError {
    message: String,
}