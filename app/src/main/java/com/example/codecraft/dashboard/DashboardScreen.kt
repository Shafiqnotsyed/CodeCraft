package com.example.codecraft.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.codecraft.R
import com.example.codecraft.data.Course
import com.example.codecraft.data.UserProgress
import com.example.codecraft.ui.components.IconFromName
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onCourseSelected: (Course) -> Unit,
    onSignOut: () -> Unit,
    onChangeCourses: () -> Unit,
    onAllTests: () -> Unit,
    onMyProfile: () -> Unit,
    onAskAi: () -> Unit,
    onPythonExercise: () -> Unit,
    onJavaExercise: () -> Unit,
    onHtmlExercise: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CodeCraft", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = colorResource(id = R.color.orange)
                ),
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.AccountCircle, "My Profile") },
                            text = { Text("My Profile") },
                            onClick = { onMyProfile(); showMenu = false }
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.List, "All Tests") },
                            text = { Text("All Tests") },
                            onClick = { onAllTests(); showMenu = false }
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.SwapHoriz, "Change Courses") },
                            text = { Text("Change Courses") },
                            onClick = { onChangeCourses(); showMenu = false }
                        )
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.Default.Forum, "Ask Craft") },
                            text = { Text("Ask Craft") },
                            onClick = { onAskAi(); showMenu = false }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, "Sign out") },
                            text = { Text("Sign out") },
                            onClick = { onSignOut(); showMenu = false }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        uiState.userProgress?.let { userProgress ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { GreetingSection(isVisible = isVisible, name = uiState.userName) }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item { LearningProgress(uiState.learningProgress, userProgress.currentCourse, isVisible = isVisible) }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item { CoursesSection(uiState.courses, userProgress.currentCourse, onCourseSelected, isVisible = isVisible) }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item {
                    ExercisesSection(
                        isVisible = isVisible,
                        selectedLanguages = userProgress.selectedLanguages,
                        onPythonExercise = onPythonExercise,
                        onJavaExercise = onJavaExercise,
                        onHtmlExercise = onHtmlExercise
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item { TestsList(userProgress, isVisible = isVisible, onStartTest = onAllTests) }
            }
        }
    }
}

@Composable
fun GreetingSection(isVisible: Boolean, name: String) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -100 },
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (name.isNotBlank()) "Welcome back, $name" else "Welcome back!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Let's continue your learning journey.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun LearningProgress(learningProgress: Float, currentCourse: Course, isVisible: Boolean) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (isVisible) learningProgress else 0f,
        label = "progressAnimation",
        animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessVeryLow)
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { 100 },
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(80.dp),
                        strokeWidth = 8.dp,
                        strokeCap = StrokeCap.Round,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = "Progress in ${currentCourse.name}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Keep up the great work!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun CoursesSection(
    courses: List<Course>,
    currentCourse: Course,
    onCourseSelected: (Course) -> Unit,
    isVisible: Boolean
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { 100 },
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Explore Courses",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(courses) { course ->
                    CourseCard(
                        course = course,
                        isSelected = course == currentCourse,
                        onCourseSelected = { onCourseSelected(course) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExercisesSection(
    isVisible: Boolean,
    selectedLanguages: Set<String>,
    onPythonExercise: () -> Unit,
    onJavaExercise: () -> Unit,
    onHtmlExercise: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { 100 },
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Practice Exercises",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            if (selectedLanguages.isEmpty()) {
                Text("No languages selected. Go to \"Change Courses\" to select one.", color = MaterialTheme.colorScheme.onBackground)
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if ("Python" in selectedLanguages) {
                        Button(onClick = onPythonExercise) { Text("Python") }
                    }
                    if ("Java" in selectedLanguages) {
                        Button(onClick = onJavaExercise) { Text("Java") }
                    }
                    if ("HTML" in selectedLanguages) {
                        Button(onClick = onHtmlExercise) { Text("HTML") }
                    }
                }
            }
        }
    }
}

@Composable
fun CourseCard(course: Course, isSelected: Boolean, onCourseSelected: () -> Unit) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        label = "cardScale",
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .size(160.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)))
            .clickable(onClick = onCourseSelected)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconFromName(
                name = course.icon,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = course.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        if (isSelected) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))
        }
    }
}

@Composable
fun TestsList(userProgress: UserProgress, isVisible: Boolean, onStartTest: () -> Unit) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { 100 },
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Next Tests for ${userProgress.currentCourse.name}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            val uncompletedTests = userProgress.currentCourse.tests.filterNot { userProgress.testScores.keys.contains(it.id) }
            if (uncompletedTests.isEmpty()) {
                Text("No new tests available for this course.", color = MaterialTheme.colorScheme.onBackground)
            } else {
                uncompletedTests.take(3).forEach { test ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PlayCircleOutline, "Not started", tint = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = test.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Button(onClick = onStartTest, shape = RoundedCornerShape(12.dp)) {
                                Text("Start")
                            }
                        }
                    }
                }
            }
        }
    }
}
