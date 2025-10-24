import org.gradle.api.Project

fun Project.namespace(): String {
    val list = mutableListOf("dev", "jason", "app", "compose", "messenger_app")

    list.add(this.name.replace('-', '_'))

    return list.joinToString(".")
}