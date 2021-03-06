import io.improbable.keanu.vertices.dbl.nonprobabilistic.ConstantDoubleVertex
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.junit.Test

class ToJsonTest {

    @Test
    fun setMirrorNormal() {
        val testParams = HelioStatParameters(
                Vector3D(1.0, 1.0, 1.0),
                HelioStatParameters.ServoParameters(0.001, 0.1, -0.02 + Math.PI / 2.0, -Math.PI/2.0),
                HelioStatParameters.ServoParameters(0.002, 0.2, Math.PI - 0.02, 0.0)
        )

        val model = HelioStat(testParams)
        val pitch = 1000
        val rotation = 345
        val normal = model.computeHeliostatNormal(ServoSetting(rotation, pitch)).getValue()

        val query = Query.SetNormal(testParams, normal)
        val jsonQuery = Json.toJson(query)

        println(jsonQuery)
    }

    @Test
    fun navigate() {
        val testParams = HelioStatParameters(
                Vector3D(1.0, 1.0, 1.0),
                HelioStatParameters.ServoParameters(0.001, 0.1, 0.015 + Math.PI / 2.0, -Math.PI / 2),
                HelioStatParameters.ServoParameters(0.002, 0.2, Math.PI - 0.03, Math.PI * 0.7)
        )

        val model = HelioStat(testParams)
        val pitch = 1234
        val rotation = 2345

        val sunVector = Vector3D(1.0, -0.1, 1.0).normalize()
        val distance = 4.0
        val correctServoSetting = ServoSetting(rotation, pitch)
        val target = model.computeTargetFromSourceDirection(
                ConstantDoubleVertex(correctServoSetting.pitch.toDouble()),
                ConstantDoubleVertex(correctServoSetting.rotation.toDouble()),
                ProbabilisticVector3D(sunVector),
                ConstantDoubleVertex(distance)).getValue()

        val currentServoSetting = ServoSetting(rotation + 100, pitch + 100)

        val query = Query.Navigation(testParams, currentServoSetting, target, sunVector)

        val queryJson = Json.toJson(query)

        println(queryJson)
    }

    @Test
    fun calibrate() {
        val testParams = HelioStatParameters(
                Vector3D(1.0, 1.0, 1.0),
                HelioStatParameters.ServoParameters(0.001, 0.1, Math.PI / 2.0, -Math.PI / 2.0),
                HelioStatParameters.ServoParameters(0.002, 0.2, Math.PI, 0.0)
        )

        val calibrationData = CalibrationData()
        calibrationData.createSyntheticTrainingSet(3, testParams)

        val calibrationDataJson = Json.toJson(calibrationData)

        println(calibrationDataJson)
    }
}
