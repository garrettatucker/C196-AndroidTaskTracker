data class Term(
    val termId: Long,
    val termName: String,
    val startDate: String,
    val endDate: String,
    val courseNames: List<Course> // List of courses associated with the term
)

data class Course(
    val courseId: Long,
    val courseName: String,
    val startDate: String,
    val endDate: String,
    val instructorName: String,
    val phoneNumber: String,
    val emailAddress: String,
    val progressStatus: String,
    val notes: String,
    val termId: Long, // Include termId
    var termName: String // Include termName
)

data class Assignment(
    val assignmentId: Long,
    val assignmentName: String,
    val startDate: String,
    val dueDate: String,
    val assessmentType: String,
    val courseId: Long
)

data class Note(
    val noteId: Long,
    val noteContent: String,
    val courseId: Long
)

