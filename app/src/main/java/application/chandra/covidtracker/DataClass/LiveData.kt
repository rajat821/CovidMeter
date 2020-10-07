package application.chandra.covidtracker.DataClass

data class LiveData (
    var state : String,
    val totalCases : String,
    val active : String,
    val recovered : String,
    val death : String,
    val todayComfirmed : String,
    val todayRecovered : String,
    val todayDeaths : String
)