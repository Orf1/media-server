package dev.orf1

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.h1
import org.slf4j.event.Level
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap


class Application(host: String, port: Int) {
    private val files: ConcurrentHashMap<String, File> = ConcurrentHashMap()
    private val folder = File("uploads/")
    private var usernameHash = hash("admin")
    private var passwordHash = hash("orf123")
    private val debug = true

    init {
        loadEnv()
        loadFolder()

        embeddedServer(Netty, host = host, port = port) {
            if (debug) {
                install(CallLogging) {
                    level = Level.DEBUG
                }
            }

            install(IgnoreTrailingSlash)

            install(Authentication) {
                basic("auth") {
                    realm = "Access to /upload and /list"
                    validate { credentials ->
                        if (validate(credentials.name, usernameHash) && validate(credentials.password, passwordHash)) {
                            UserIdPrincipal(credentials.name)
                        } else {
                            null
                        }
                    }
                }
            }

            routing {
                get("/") {
                    call.respondRedirect("/upload", false)
                }

                get("/list") {
                    val p: String = if (port != 80) {
                        ":$port"
                    } else {
                        ""
                    }
                    var f = ""
                    files.forEach { entry ->
                        f += "$host$p/uploads/${entry.key}\n"
                    }
                    call.respond(f)
                }

                get("/uploads/{id}") {
                    val id = call.parameters["id"]!!
                    if (files.containsKey(id)) {
                        files[id]?.let { it1 -> call.respondFile(it1) }
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }

                authenticate("auth") {
                    static("pages/upload") {
                        resources("pages/upload")
                    }

                    resource("/upload", "pages/upload/index.html")

                    post("/upload") {
                        val multipartData = call.receiveMultipart()
                        multipartData.forEachPart { part ->
                            when (part) {
                                is PartData.FileItem -> {
                                    val id = generateFileId()
                                    val fileName =
                                        "" + id + part.originalFileName?.let { it1 -> getFileExtensionFromString(it1) }
                                    val fileBytes = part.streamProvider().readBytes()
                                    if (fileBytes.isNotEmpty()) {
                                        val file = File("uploads/$fileName")
                                        file.writeBytes(fileBytes)
                                        files[file.nameWithoutExtension] = file
                                        call.respondHtml {
                                            body {
                                                h1 {
                                                    text("Uploaded file. Access using the following link: ")
                                                    a("/uploads/$id") { +"link" }
                                                }
                                            }
                                        }
                                    } else {
                                        call.respond(HttpStatusCode.BadRequest)
                                    }
                                }
                                else -> {
                                    call.respond(HttpStatusCode.BadRequest)
                                }
                            }
                        }
                    }
                }
            }
        }.start(wait = true)
    }

    private fun loadFolder() {
        if (folder.exists()) {
            val fileList = folder.listFiles()
            if (fileList?.isNotEmpty() == true) {
                fileList.forEach { file ->
                    files[file.nameWithoutExtension] = file
                    println("Loaded file: " + file.name)
                }
            }
        } else {
            folder.mkdir()
        }
    }

    private fun validate(text: String, hash: String): Boolean {
        return hash(text) == hash
    }

    private fun hash(input: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())

        return bytesToHex(bytes)
    }

    private fun bytesToHex(hash: ByteArray): String {
        val hexString = StringBuilder(2 * hash.size)
        for (i in hash.indices) {
            val hex = Integer.toHexString(0xff and hash[i].toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()
    }

    private fun getFileExtensionFromString(name: String): String {
        val lastIndexOf = name.lastIndexOf(".")
        return if (lastIndexOf == -1) {
            ""
        } else name.substring(lastIndexOf)
    }

    private fun generateFileId(): String {
        var id = generateRandomString()
        while (files.containsKey(id)) {
            id = generateRandomString()
        }
        return id
    }

    private fun generateRandomString(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..8)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun loadEnv() {
        val env = System.getenv()
        val passwordHashEnv = env["MEDIA_SERVER_PASSWORD_HASH"]
        val usernameHashEnv = env["MEDIA_SERVER_USERNAME_HASH"]
        if(passwordHashEnv != null) {
            passwordHash = passwordHashEnv
        }
        if(usernameHashEnv != null) {
            usernameHash = usernameHashEnv
        }
    }
}

fun main() {
    Application("0.0.0.0", 80)
}