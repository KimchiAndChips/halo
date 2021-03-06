import io.javalin.ApiBuilder.get
import io.javalin.ApiBuilder.post
import io.javalin.Javalin
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D

class Server {

    val app = Javalin.create().apply {
        port(8080)
        exception(Exception::class.java) { e, ctx -> e.printStackTrace() }
        error(404) { ctx -> ctx.json("not found") }
    }

    constructor() {
        app.routes {
            get("/ping") { ctx ->
                ctx.json("pong")
            }

            post("/ping") { ctx ->
                ctx.json("pong")
            }

            post("/setMirrorNormal") { ctx ->
                val query = Json.fromJson(ctx.body(), Query.SetNormal::class.java)
                ctx.status(201)

                val navigator = HelioStatNavigator(query.params)
                val servoSetting = navigator.normalToServoSignal(query.mirrorNormal)
                ctx.json(servoSetting)
            }

            post("/navigate") { ctx ->
                val query = Json.fromJson(ctx.body(), Query.Navigation::class.java)
                ctx.status(201)

                val navigator = HelioStatNavigator(query.params)
                val servoSetting = navigator.computeServoSettingFromDirection(query.source, query.targetPoint, query.currentServoSetting)
                ctx.json(servoSetting)
            }

            post("/navigateSunToPoint") { ctx ->
                val query = Json.fromJson(ctx.body(), Query.Navigation::class.java)
                ctx.status(201)

                val navigator = HelioStatNavigator(query.params)
                val servoSetting = navigator.computeServoSettingFromDirection(Astronomy.getSolarRayCartesian(), query.targetPoint, query.currentServoSetting)
                ctx.json(servoSetting)
            }


            post("/navigatePointToPoint") { ctx ->
                val query = Json.fromJson(ctx.body(), Query.Navigation::class.java)
                ctx.status(201)

                val navigator = HelioStatNavigator(query.params)
                val servoSetting = navigator.computeServoSettingFromPoint(query.source, query.targetPoint, query.currentServoSetting)
                ctx.json(servoSetting)
            }

            post("/calibrate") { ctx ->
                //                val calibrationData = Json.fromJson(ctx.body(), CalibrationData::class.java)
                val rawData = Json.fromJson(ctx.body(), CalibrationRawData2::class.java)
                ctx.status(201)

                val calibrationData = CalibrationData.fromCalibrationRawData2(rawData)

                var calib = HelioStatCalibrator(calibrationData)
                var params = calib.inferHelioStatParams()

                // todo Get one with no dodgy data points and see the residual. Multiply by 5 and set that as a threshold.
                var avResidual = calib.calculateResiduals(params).sumByDouble(Vector3D::getNorm) / calib.calibrationData.size

                val paramsJson = Json.toJson(params)

                // Pretend this is html to get around the in-built Jackson json serialization in Javalin, which doesn't
                // seem to handle complex objects well.
                ctx.html(paramsJson)
            }

            post("/ransacCalibrate") { ctx ->
                //                val calibrationData = Json.fromJson(ctx.body(), CalibrationData::class.java)
                val rawData = Json.fromJson(ctx.body(), CalibrationRawData2::class.java)
                ctx.status(201)

                val calibrationData = CalibrationData.fromCalibrationRawData2(rawData)

                var calib = HelioStatCalibrator(calibrationData)
                var params = calib.inferHelioStatParamsRANSAC()

                // todo Get one with no dodgy data points and see the residual. Multiply by 5 and set that as a threshold.
                var avResidual = calib.calculateResiduals(params).sumByDouble(Vector3D::getNorm) / calib.calibrationData.size

                val paramsJson = Json.toJson(params)

                // Pretend this is html to get around the in-built Jackson json serialization in Javalin, which doesn't
                // seem to handle complex objects well.
                ctx.html(paramsJson)
            }

            post("/calibrateInternalDataFormat") { ctx ->
                val calibrationData = Json.fromJson(ctx.body(), CalibrationData::class.java)
                ctx.status(201)

                var calib = HelioStatCalibrator(calibrationData)
                var params = calib.inferHelioStatParams()

                // todo Get one with no dodgy data points and see the residual. Multiply by 5 and set that as a threshold.
                var avResidual = calib.calculateResiduals(params).sumByDouble(Vector3D::getNorm) / calib.calibrationData.size

                val paramsJson = Json.toJson(params)

                // Pretend this is html to get around the in-built Jackson json serialization in Javalin, which doesn't
                // seem to handle complex objects well.
                ctx.html(paramsJson)
            }
        }
    }

    fun start(): Javalin {
        return app.start()
    }
}
