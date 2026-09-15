package com.example.data

data class PoseTemplate(
    val id: String,
    val title: String,
    val category: String,
    val imageUrl: String
)

object PoseTemplateRepository {
    val categories = listOf("All", "Saved")

    val templates = listOf(
        PoseTemplate("t1", "Pose 1", "Saved", "https://i.pinimg.com/736x/d4/99/e6/d499e650eda86f174b437690cb65af1e.jpg"),
        PoseTemplate("t2", "Pose 2", "Saved", "https://i.pinimg.com/736x/06/40/d2/0640d2ba1d77c47eda6755b31a046747.jpg"),
        PoseTemplate("t3", "Pose 3", "Saved", "https://i.pinimg.com/736x/c7/e4/5c/c7e45c43102b0988430a614afe5b4de3.jpg"),
        PoseTemplate("t4", "Pose 4", "Saved", "https://i.pinimg.com/736x/f4/d5/2e/f4d52ee30e032b0d4a7f6c3946869675.jpg"),
        PoseTemplate("t5", "Pose 5", "Saved", "https://i.pinimg.com/736x/c5/dd/60/c5dd603a4d34e54fa15465c7833c9cde.jpg")
    )
}
