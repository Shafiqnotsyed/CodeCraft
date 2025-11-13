package com.example.codecraft.tests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.codecraft.data.Question
import com.example.codecraft.data.UserProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// These imports were missing
import com.example.codecraft.tests.TestsUiState
import com.example.codecraft.tests.TestCategoryResult
import com.example.codecraft.tests.LanguageTestGroup

class TestsViewModel(
    private val userProgressRepository: UserProgressRepository
) : ViewModel() {

    private val _allTestCategories = MutableStateFlow<List<String>>(emptyList())
    private val _currentTestCategory = MutableStateFlow<String?>(null)
    private val _userAnswers = MutableStateFlow<Map<String, String>>(emptyMap())
    private val _isTestSubmitted = MutableStateFlow(false)
    private val _showCompletionScreen = MutableStateFlow(false)
    private val _lastTestScore = MutableStateFlow(0)

    private val _uiState = MutableStateFlow(TestsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _allTestCategories.value = listOf(
            "Java quiz 1", "Java quiz 2", "Java quiz 3", "Java quiz 4", "Java quiz 5",
            "Python quiz 1", "Python quiz 2", "Python quiz 3", "Python quiz 4", "Python quiz 5",
            "HTML quiz 1", "HTML quiz 2", "HTML quiz 3", "HTML quiz 4", "HTML quiz 5"
        )

        viewModelScope.launch {
            combine(
                _allTestCategories,
                userProgressRepository.userProgress
            ) { categories, userProgress ->
                val selectedLanguages = userProgress?.selectedLanguages ?: emptySet()
                val testScores = userProgress?.testScores ?: emptyMap()
                val filteredCategories = categories.filter { category ->
                    selectedLanguages.any { language -> category.startsWith(language, ignoreCase = true) }
                }
                val results = filteredCategories.map { category ->
                    val questions = getQuestionsForCategory(category)
                    val score = testScores[category]
                    TestCategoryResult(
                        category = category,
                        score = score,
                        totalQuestions = questions.size
                    )
                }
                val groupedResults = results.groupBy { it.category.split(" ").first() }
                    .map { (language, tests) ->
                        LanguageTestGroup(language, tests)
                    }
                _uiState.update { it.copy(testCategoryResults = results, groupedAndFilteredLanguages = groupedResults) }
            }.collect {}
        }

        viewModelScope.launch {
            combine(
                _currentTestCategory,
                _userAnswers,
                _isTestSubmitted,
                _lastTestScore,
                _showCompletionScreen
            ) { category, answers, isSubmitted, score, showCompletion ->
                _uiState.update {
                    it.copy(
                        currentTestCategory = category,
                        currentTestQuestions = getQuestionsForCategory(category ?: ""),
                        userAnswers = answers,
                        isTestSubmitted = isSubmitted,
                        lastTestScore = score,
                        lastTestTotalQuestions = getQuestionsForCategory(category ?: "").size,
                        showCompletionScreen = showCompletion
                    )
                }
            }.collect {}
        }
    }

    fun selectTestCategory(category: String) {
        viewModelScope.launch {
            _currentTestCategory.value = category
            val progress = userProgressRepository.userProgress.first()
            val score = progress?.testScores?.get(category)
            if (score != null) {
                _isTestSubmitted.value = true
                _lastTestScore.value = score
            } else {
                _isTestSubmitted.value = false
                _lastTestScore.value = 0
                _userAnswers.value = emptyMap()
            }
        }
    }

    fun clearSelectedCategory() {
        _currentTestCategory.value = null
    }

    fun onAnimationFinished() {
        _showCompletionScreen.value = false
        clearSelectedCategory()
    }

    fun onAnswerSelected(questionId: String, answer: String) {
        if (!_isTestSubmitted.value) {
            _userAnswers.update { currentAnswers ->
                currentAnswers.toMutableMap().apply {
                    this[questionId] = answer
                }
            }
        }
    }

    fun submitTest() {
        viewModelScope.launch {
            var correctAnswers = 0
            val questionsList = getQuestionsForCategory(_currentTestCategory.value ?: "")
            val userAnswersMap = _userAnswers.value

            for (question in questionsList) {
                if (userAnswersMap[question.id] == question.correctAnswer) {
                    correctAnswers++
                }
            }
            val category = _currentTestCategory.value!!
            val currentProgress = userProgressRepository.userProgress.first()
            if (currentProgress != null) {
                val updatedScores = currentProgress.testScores.toMutableMap()
                updatedScores[category] = correctAnswers
                val updatedProgress = currentProgress.copy(
                    testScores = updatedScores
                )
                userProgressRepository.updateUserProgress(updatedProgress)
            }

            _lastTestScore.value = correctAnswers
            _isTestSubmitted.value = true
            _showCompletionScreen.value = true
        }
    }

    fun resetTest() {
        viewModelScope.launch {
            val category = _currentTestCategory.value
            if (category != null) {
                val currentProgress = userProgressRepository.userProgress.first()
                if (currentProgress != null) {
                    val updatedScores = currentProgress.testScores.toMutableMap()
                    updatedScores.remove(category)
                    val updatedProgress = currentProgress.copy(
                        testScores = updatedScores
                    )
                    userProgressRepository.updateUserProgress(updatedProgress)
                }
            }
            _isTestSubmitted.value = false
            _lastTestScore.value = 0
            _userAnswers.value = emptyMap()
        }
    }

    private fun getQuestionsForCategory(category: String): List<Question> {
        return when (category) {
            "Java quiz 1" -> getJavaQuestions1()
            "Java quiz 2" -> getJavaQuestions2()
            "Java quiz 3" -> getJavaQuestions3()
            "Java quiz 4" -> getJavaQuestions4()
            "Java quiz 5" -> getJavaQuestions5()
            "Python quiz 1" -> getPythonQuestions1()
            "Python quiz 2" -> getPythonQuestions2()
            "Python quiz 3" -> getPythonQuestions3()
            "Python quiz 4" -> getPythonQuestions4()
            "Python quiz 5" -> getPythonQuestions5()
            "HTML quiz 1" -> getHtmlQuestions1()
            "HTML quiz 2" -> getHtmlQuestions2()
            "HTML quiz 3" -> getHtmlQuestions3()
            "HTML quiz 4" -> getHtmlQuestions4()
            "HTML quiz 5" -> getHtmlQuestions5()
            else -> emptyList()
        }
    }

    private fun getJavaQuestions1(): List<Question> {
        return listOf(
            Question("j1", "Which of the following is not a Java keyword?", listOf("static", "try", "string", "new"), "string"),
            Question("j2", "What is the size of an int in Java?", listOf("16 bits", "32 bits", "64 bits", "128 bits"), "32 bits"),
            Question("j3", "Which of these is used to access a superclass method?", listOf("super", "this", "self", "parent"), "super"),
            Question("j4", "Which of the following is a primitive data type?", listOf("String", "int", "Array", "Object"), "int"),
            Question("j5", "What is the entry point for any Java program?", listOf("main()", "start()", "run()", "System.in"), "main()"),
        )
    }

    private fun getJavaQuestions2(): List<Question> {
        return listOf(
            Question("j6", "Which keyword is used to prevent a class from being inherited?", listOf("final", "static", "private", "abstract"), "final"),
            Question("j7", "What is the return type of a constructor?", listOf("int", "void", "class", "It doesn't have a return type"), "It doesn't have a return type"),
            Question("j8", "Which of these is an interface for sorting objects?", listOf("Comparable", "Sortable", "Comparator", "Iterable"), "Comparable"),
            Question("j9", "Which package is implicitly imported into every Java program?", listOf("java.util", "java.io", "java.lang", "java.net"), "java.lang"),
            Question("j10", "What does the 'static' keyword mean for a method?", listOf("The method can be overridden", "The method belongs to the class, not an instance", "The method cannot be accessed from outside the class", "The method is final"), "The method belongs to the class, not an instance")
        )
    }

    private fun getJavaQuestions3(): List<Question> {
        return listOf(
            Question("j11", "Which operator is used to test if an object is an instance of a specific class?", listOf("instanceof", "typeof", "is", "instance"), "instanceof"),
            Question("j12", "In a try-catch block, which block is always executed, regardless of whether an exception is thrown?", listOf("try", "catch", "finally", "execute"), "finally"),
            Question("j13", "What's the main difference between an ArrayList and a LinkedList?", listOf("ArrayList is synchronized", "LinkedList provides faster random access", "ArrayList provides faster insertion and deletion at the ends", "LinkedList provides faster insertion and deletion in the middle"), "LinkedList provides faster insertion and deletion in the middle"),
            Question("j14", "An abstract class can have constructors, but an interface cannot.", listOf("True", "False"), "True"),
            Question("j15", "Which statement is used to exit a loop or a switch statement?", listOf("exit", "break", "continue", "return"), "break")
        )
    }

    private fun getJavaQuestions4(): List<Question> {
        return listOf(
            Question("j16", "For mutable strings in Java, which class is preferred?", listOf("String", "StringBuilder", "StringConstant", "MutableString"), "StringBuilder"),
            Question("j17", "What is it called when two or more methods in the same class have the same name but different parameters?", listOf("Overriding", "Overloading", "Inheritance", "Encapsulation"), "Overloading"),
            Question("j18", "Can a HashMap contain duplicate keys?", listOf("Yes", "No", "Only if the values are different", "Only for null keys"), "No"),
            Question("j19", "Which part of `public static void main(String[] args)` allows the method to be called without creating an object?", listOf("public", "static", "void", "main"), "static"),
            Question("j20", "The process of reclaiming memory of objects that are no longer in use is called:", listOf("Memory Allocation", "Garbage Collection", "Defragmentation", "Stacking"), "Garbage Collection")
        )
    }

    private fun getJavaQuestions5(): List<Question> {
        return listOf(
            Question("j21", "Exceptions that are checked at compile time are called:", listOf("Checked Exceptions", "Unchecked Exceptions", "Runtime Exceptions", "Errors"), "Checked Exceptions"),
            Question("j22", "What is the difference between `equals()` and `==` for objects?", listOf("No difference", "`==` compares object references, `equals()` compares content", "`equals()` compares object references, `==` compares content", "`==` is faster"), "`==` compares object references, `equals()` compares content"),
            Question("j23", "Which is a special method used to initialize objects?", listOf("Function", "Constructor", "Initializer", "Builder"), "Constructor"),
            Question("j24", "What is the purpose of the `default` case in a switch statement?", listOf("It is mandatory", "It handles all cases not explicitly defined", "It sets the default value for the switch variable", "It is executed first"), "It handles all cases not explicitly defined"),
            Question("j25", "What is a 'wrapper class' in Java?", listOf("A class that secures other classes", "A class that provides an object representation of a primitive type", "A UI container class", "A class for testing purposes"), "A class that provides an object representation of a primitive type")
        )
    }

    private fun getPythonQuestions1(): List<Question> {
        return listOf(
            Question("p1", "What is the output of `print(2 ** 3)`?", listOf("6", "8", "9", "12"), "8"),
            Question("p2", "Which keyword is used to define a function in Python?", listOf("fun", "def", "function", "define"), "def"),
            Question("p3", "Which of the following is used to comment a single line in Python?", listOf("//", "#", "/* */", "--"), "#"),
            Question("p4", "What is the data type of the result of `5 / 2` in Python 3?", listOf("int", "float", "double", "string"), "float"),
            Question("p5", "Which collection is ordered, changeable, and allows duplicate members?", listOf("List", "Tuple", "Set", "Dictionary"), "List"),
        )
    }

    private fun getPythonQuestions2(): List<Question> {
        return listOf(
            Question("p6", "How do you get the length of a list named `my_list`?", listOf("my_list.length()", "len(my_list)", "length(my_list)", "my_list.size"), "len(my_list)"),
            Question("p7", "Which of the following creates a tuple?", listOf("[1, 2, 3]", "{1, 2, 3}", "(1, 2, 3)", "<1, 2, 3>"), "(1, 2, 3)"),
            Question("p8", "What is the operator for checking if a value is NOT in a list?", listOf("not in", "!=", "<>", "not_in"), "not in"),
            Question("p9", "How do you start a block of code for an if statement?", listOf("With a curly bracket {", "By indenting the code block", "Using the 'begin' keyword", "With a parenthesis ("), "By indenting the code block"),
            Question("p10", "Which function converts a string to an integer?", listOf("to_int()", "parseInt()", "int()", "string_to_int()"), "int()"),
        )
    }

    private fun getPythonQuestions3(): List<Question> {
        return listOf(
            Question("p11", "Which function is used to get input from the user?", listOf("input()", "get()", "read()", "prompt()"), "input()"),
            Question("p12", "Which data structure is a collection of key-value pairs?", listOf("List", "Tuple", "Set", "Dictionary"), "Dictionary"),
            Question("p13", "Which keyword is used for a multi-way conditional statement?", listOf("else if", "other", "elif", "case"), "elif"),
            Question("p14", "What does `range(3)` generate?", listOf("1, 2, 3", "0, 1, 2, 3", "0, 1, 2", "1, 2"), "0, 1, 2"),
            Question("p15", "Which is the modern and preferred way to format strings in Python?", listOf("f-strings", "%-formatting", "str.format()", "string concatenation"), "f-strings")
        )
    }

    private fun getPythonQuestions4(): List<Question> {
        return listOf(
            Question("p16", "What does the `pass` statement do?", listOf("It exits the program", "It skips the current loop iteration", "It acts as a placeholder for no operation", "It raises an exception"), "It acts as a placeholder for no operation"),
            Question("p17", "How do you import a module named `math`?", listOf("include math", "import math", "use math", "require math"), "import math"),
            Question("p18", "What is the difference between the `==` and `is` operators?", listOf("They are identical", "`is` compares identity, `==` compares equality", "`==` compares identity, `is` compares equality", "`is` is for numbers only"), "`is` compares identity, `==` compares equality"),
            Question("p19", "How do you open a file named 'data.txt' for reading?", listOf("open('data.txt', 'r')", "open('data.txt', 'w')", "read('data.txt')", "open.file('data.txt')"), "open('data.txt', 'r')"),
            Question("p20", "In a class method, what does the `self` parameter refer to?", listOf("The class itself", "The current instance of the class", "The parent class", "The global scope"), "The current instance of the class")
        )
    }

    private fun getPythonQuestions5(): List<Question> {
        return listOf(
            Question("p21", "What is a concise way to create lists, often from other iterables?", listOf("For loops", "List comprehensions", "Generators", "Map functions"), "List comprehensions"),
            Question("p22", "What does `my_list[-1]` return?", listOf("The first element", "The last element", "An error", "The second to last element"), "The last element"),
            Question("p23", "Which method removes the first occurrence of a value from a list?", listOf("pop()", "remove()", "del()", "discard()"), "remove()"),
            Question("p24", "What does the `*args` syntax in a function definition allow?", listOf("Passing a list of arguments", "Passing a variable number of positional arguments", "Passing a dictionary of arguments", "Passing only keyword arguments"), "Passing a variable number of positional arguments"),
            Question("p25", "A common tool to create isolated Python environments is called:", listOf("docker", "venv", "pyenv", "isolate"), "venv")
        )
    }

    private fun getHtmlQuestions1(): List<Question> {
        return listOf(
            Question("h1", "What does HTML stand for?", listOf("Hyper Trainer Marking Language", "Hyper Text Marketing Language", "Hyper Text Markup Language", "Hyperlink and Text Markup Language"), "Hyper Text Markup Language"),
            Question("h2", "Which HTML tag is used to define an internal style sheet?", listOf("<style>", "<script>", "<css>", "<link>"), "<style>"),
            Question("h3", "Which HTML attribute specifies an alternate text for an image, if the image cannot be displayed?", listOf("src", "alt", "title", "href"), "alt"),
            Question("h4", "Which HTML element is used to specify a header for a document or section?", listOf("<head>", "<header>", "<h1>", "<section>"), "<header>"),
            Question("h5", "Which HTML element defines navigation links?", listOf("<nav>", "<navigate>", "<navigation>", "<navlinks>"), "<nav>"),
        )
    }

    private fun getHtmlQuestions2(): List<Question> {
        return listOf(
            Question("h6", "Which tag is used to create a hyperlink?", listOf("<a>", "<link>", "<href>", "<hyperlink>"), "<a>"),
            Question("h7", "What is the correct tag for an ordered list?", listOf("<ul>", "<ol>", "<list>", "<li>"), "<ol>"),
            Question("h8", "Which element is used for the largest heading?", listOf("<h6>", "<head>", "<h1>", "<heading>"), "<h1>"),
            Question("h9", "Which tag is used to define a table row?", listOf("<td>", "<tr>", "<th>", "<table>"), "<tr>"),
            Question("h10", "Which tag is used to define a table cell?", listOf("<td>", "<tr>", "<th>", "<cell>"), "<td>"),
        )
    }

    private fun getHtmlQuestions3(): List<Question> {
        return listOf(
            Question("h11", "What is the purpose of the '<!DOCTYPE html>' declaration?", listOf("It defines a variable", "It specifies the HTML version being used", "It creates a comment", "It links a stylesheet"), "It specifies the HTML version being used"),
            Question("h12", "Which element is an inline element by default?", listOf("<div>", "<span>", "<p>", "<h1>"), "<span>"),
            Question("h13", "How do you add a comment in an HTML file?", listOf("<!-- This is a comment -->", "// This is a comment", "/* This is a comment */", "# This is a comment"), "<!-- This is a comment -->"),
            Question("h14", "Which input type defines a button for submitting form data?", listOf("button", "submit", "send", "ok"), "submit"),
            Question("h15", "What does the 'target=\"_blank\"' attribute do in an '<a>' tag?", listOf("Opens the link in a new tab or window", "Opens the link in the same frame", "Opens the link in the parent frame", "Targets a specific div"), "Opens the link in a new tab or window")
        )
    }

    private fun getHtmlQuestions4(): List<Question> {
        return listOf(
            Question("h16", "Which tag is used to insert a line break?", listOf("<break>", "<lb>", "<br>", "<newline>"), "<br>"),
            Question("h17", "Which tag is used to define a container for user input?", listOf("<form>", "<input>", "<container>", "<fieldset>"), "<form>"),
            Question("h18", "What is the purpose of the 'lang' attribute in the '<html>' tag?", listOf("To specify the language of the page content", "To set the server language", "To define the page's color scheme", "To specify the character set"), "To specify the language of the page content"),
            Question("h19", "An unordered list starts with which tag?", listOf("<ol>", "<ul>", "<list>", "<li>"), "<ul>"),
            Question("h20", "Which attribute of the '<img>' tag is required?", listOf("alt", "title", "src", "style"), "src")
        )
    }

    private fun getHtmlQuestions5(): List<Question> {
        return listOf(
            Question("h21", "Which HTML5 element is used to embed video content?", listOf("<media>", "<movie>", "<video>", "<source>"), "<video>"),
            Question("h22", "Elements like '<article>', '<section>', and '<aside>' are examples of:", listOf("Inline elements", "Styling elements", "Semantic elements", "Form elements"), "Semantic elements"),
            Question("h23", "The root element of an HTML table is:", listOf("<tr>", "<td>", "<table>", "<thead>"), "<table>"),
            Question("h24", "What does '<meta charset=\"UTF-8\">' do?", listOf("It sets the text color to black", "It declares the character encoding for the document", "It enables JavaScript", "It defines metadata for search engines"), "It declares the character encoding for the document"),
            Question("h25", "Which section of the HTML document contains metadata and links to scripts and stylesheets?", listOf("<body>", "<footer>", "<header>", "<head>"), "<head>")
        )
    }
}

class TestsViewModelFactory(
    private val userProgressRepository: UserProgressRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TestsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TestsViewModel(userProgressRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
