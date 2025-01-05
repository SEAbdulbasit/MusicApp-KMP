package musicapp.player

expect class CountDownTimer(
    initialMillis: Long,
    intervalMillis: Long,
    onCountDownFinish: () -> Unit
) {
    fun start()
    fun cancel()
}