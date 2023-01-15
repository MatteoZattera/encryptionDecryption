import java.io.File
import kotlin.math.sign

/** Prints `this` argument to the standard output stream. */
fun <T> T.printlnIt() = println(this)

/** Returns `this` string encrypted. */
fun String.encrypted(key: Int, alg: String) = if (alg == "unicode") this.map { it + key }.joinToString("") else this.map {
    if (it.isLowerCase()) {
        var encryptedChar = it + key
        while (encryptedChar !in 'a'..'z') encryptedChar -= 26 * key.sign
        encryptedChar
    } else it
}.joinToString("")

/** Returns `this` string decrypted. */
fun String.decrypted(key: Int, alg: String) = if (alg == "unicode") this.map { it - key }.joinToString("") else this.map {
    if (it.isLowerCase()) {
        var encryptedChar = it - key
        while (encryptedChar !in 'a'..'z') encryptedChar += 26 * key.sign
        encryptedChar
    } else it
}.joinToString("")

fun main(args: Array<String>) {

    val commands = listOf("-key", "-data", "-mode", "-in", "-out", "-alg")

    val key = args.indexOf("-key").run { if (this != -1 && args.lastIndex > this) args[this + 1].toIntOrNull() else 0 } ?: 0
    val data = args.indexOf("-data").run { if (this != -1 && args.lastIndex > this) args[this + 1] else "" }
    val alg = args.indexOf("-alg").run { if (this != -1 && args.lastIndex > this) args[this + 1] else "shift" }
    val mode = args.indexOf("-mode")
        .run { if (this != -1 && args.lastIndex > this) args[this + 1] else "enc" }
        .takeIf { it in listOf("enc", "dec") } ?: "enc"

    val inFile = args.indexOf("-in")
        .run { if (this != -1) this else null }
        ?.run { if (args.lastIndex > this && args[this + 1] !in commands) args[this + 1] else "Error: missing input file name.".printlnIt().let { return } }
        ?.run { File(this).takeIf { it.exists() } ?: "Error: input file does not exist.".printlnIt().let { return } }

    val outFile = args.indexOf("-out")
        .run { if (this != -1) this else null }
        ?.run { if (args.lastIndex > this && args[this + 1] !in commands) args[this + 1] else "Error: missing output file name.".printlnIt().let { return } }
        ?.run { File(this) }

    if (inFile == null) {
        if (outFile == null) with(data) { if (mode == "enc") encrypted(key, alg) else decrypted(key, alg) }.printlnIt()
        else outFile.writeText(with(data) { if (mode == "enc") encrypted(key, alg) else decrypted(key, alg) })
    } else {
        if (outFile == null) with(inFile.readText()) { if (mode == "enc") encrypted(key, alg) else decrypted(key, alg) }.printlnIt()
        else outFile.writeText(with(inFile.readText()) { if (mode == "enc") encrypted(key, alg) else decrypted(key, alg) })
    }
}
