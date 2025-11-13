package com.example.codecraft.userinterface.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.codecraft.domain.rules.Exercise
import com.example.codecraft.userinterface.ai.AiAssist
import com.example.codecraft.userinterface.components.CodeEditor

// Seed beginner Java exercises
private val javaExercises = listOf(
    Exercise(
        id = "grade_calculator",
        title = "Student Grade Calculator",
        description = """
            Build a program that:
            - Takes marks for 5 subjects from the user
            - Calculates the total, average, and grade:
              A: 80–100
              B: 70–79
              C: 60–69
              D: 50–59
              F: below 50
        """.trimIndent(),
        starterCode = """
            public class GradeCalculator {
                public static void main(String[] args) {
                    // your code here
                }
            }
        """.trimIndent(),
        solutionCode = """
            import java.util.Scanner;
            public class GradeCalculator {
                public static void main(String[] args) {
                    Scanner sc = new Scanner(System.in);
                    int total = 0;
                    for (int i = 1; i <= 5; i++) {
                        System.out.print("Enter marks for subject " + i + ": ");
                        total += sc.nextInt();
                    }
                    double average = total / 5.0;
                    char grade;
                    if (average >= 80) grade = 'A';
                    else if (average >= 70) grade = 'B';
                    else if (average >= 60) grade = 'C';
                    else if (average >= 50) grade = 'D';
                    else grade = 'F';
                    System.out.println("Total: " + total);
                    System.out.println("Average: " + average);
                    System.out.println("Grade: " + grade);
                }
            }
        """.trimIndent()
    ),
    Exercise(
        id = "bank_account",
        title = "Bank Account Simulation",
        description = """
            Create a BankAccount class that:
            - Stores balance and accountNumber
            - Has methods: deposit(), withdraw(), displayBalance()
            - Prevents withdrawing more than the current balance
        """.trimIndent(),
        starterCode = """
            public class BankAccount {
                // your code here
            }
        """.trimIndent(),
        solutionCode = """
            public class BankAccount {
                private double balance;
                private String accountNumber;

                public BankAccount(String accountNumber, double balance) {
                    this.accountNumber = accountNumber;
                    this.balance = balance;
                }

                public void deposit(double amount) {
                    balance += amount;
                }

                public void withdraw(double amount) {
                    if (amount <= balance) {
                        balance -= amount;
                    } else {
                        System.out.println("Insufficient funds");
                    }
                }

                public void displayBalance() {
                    System.out.println("Account " + accountNumber + " balance: " + balance);
                }
            }
        """.trimIndent()
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JavaExercisesScreen() {
    var selected by remember { mutableStateOf<Exercise?>(null) }
    var code by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }
    var showSolution by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Java Exercises") }) }) { p ->
        Column(
            Modifier
                .padding(p)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selected == null) {
                Text("Choose an exercise:", style = MaterialTheme.typography.titleMedium)
                javaExercises.forEach { ex ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selected = ex
                                code = ex.starterCode
                                feedback = null
                                showSolution = false
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(ex.title, style = MaterialTheme.typography.titleLarge)
                            Text(ex.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                Text(selected!!.description, style = MaterialTheme.typography.titleLarge)

                CodeEditor(
                    value = code,
                    onChange = { code = it },
                    hint = "Type your Java code here..."
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            feedback =
                                if (code.filter { !it.isWhitespace() } ==
                                    selected!!.solutionCode.filter { !it.isWhitespace() }) {
                                    "✅ Correct!"
                                } else {
                                    "❌ Incorrect. Try again or view the solution."
                                }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Run Code") }

                    OutlinedButton(
                        onClick = { showSolution = true },
                        modifier = Modifier.weight(1f)
                    ) { Text("Show Answer") }
                }

                // ---------- AI Assist (safe: no nulls passed) ----------
                val diagnosticsText = feedback ?: ""
                AiAssist(
                    language = "Java",
                    code = code,
                    diagnostics = diagnosticsText,
                    lessonTitle = selected?.title ?: ""      // OK if your Exercise has title
                    // lessonDescription omitted (default "")
                )

                feedback?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (it.contains("Correct")) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }

                if (showSolution) {
                    Spacer(Modifier.height(8.dp))
                    Text("Solution:", style = MaterialTheme.typography.titleMedium)
                    Surface(
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            selected!!.solutionCode,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
