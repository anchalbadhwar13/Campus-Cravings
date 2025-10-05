package com.example.campuscravings.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuscravings.R
import com.example.campuscravings.data.model.UserRole
import kotlinx.coroutines.delay
import kotlin.ranges.rangeTo
import kotlin.text.toFloat
import kotlin.times
import kotlin.unaryMinus

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Entrance animations
    var logoVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var cardVisible by remember { mutableStateOf(false) }
    var footerVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        logoVisible = true
        delay(300)
        titleVisible = true
        delay(200)
        cardVisible = true
        delay(300)
        footerVisible = true
    }

    // Handle successful authentication
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onAuthSuccess()
        }
    }

    // Background with animated gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFEAF0FF),
                        Color(0xFFFDF6F6),
                        Color(0xFFE8F4F8)
                    ),
                    center = androidx.compose.ui.geometry.Offset(
                        animatedOffset,
                        animatedOffset
                    )
                )
            )
    ) {
        // Floating food emojis background
        FloatingFoodEmojis()

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated logo
            AnimatedVisibility(
                visible = logoVisible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                AnimatedBurgerIcon()
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Animated title
            AnimatedVisibility(
                visible = titleVisible,
                enter = slideInVertically() + fadeIn()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(
                                color = Color(0xFF1563F7),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 38.sp,
                                fontStyle = FontStyle.Italic
                            )) {
                                append("Campus")
                            }
                            append(" ")
                            withStyle(SpanStyle(
                                color = Color(0xFF1563F7),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 38.sp,
                                fontStyle = FontStyle.Italic
                            )) {
                                append("Cravings")
                            }
                        },
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Food delivery for students, by students",
                        fontSize = 18.sp,
                        color = Color(0xFF5B6B7A),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }

            // Animated card
            AnimatedVisibility(
                visible = cardVisible,
                enter = slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ) + fadeIn()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                    // Remove the graphicsLayer with shadowElevation
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedContent(
                            targetState = isSignUp,
                            transitionSpec = {
                                slideInHorizontally { width ->
                                    if (targetState) width else -width
                                } + fadeIn() togetherWith
                                        slideOutHorizontally { width ->
                                            if (targetState) -width else width
                                        } + fadeOut()
                            },
                            label = "form_transition"
                        ) { targetIsSignUp ->
                            if (targetIsSignUp) {
                                SignUpForm(
                                    viewModel = viewModel,
                                    onSwitchToSignIn = { isSignUp = false },
                                    uiState = uiState
                                )
                            } else {
                                SignInForm(
                                    viewModel = viewModel,
                                    onSwitchToSignUp = { isSignUp = true },
                                    uiState = uiState
                                )
                            }
                        }
                    }
                }
            }


                Spacer(modifier = Modifier.height(16.dp))

            // Animated footer
            AnimatedVisibility(
                visible = footerVisible,
                enter = fadeIn() + slideInVertically()
            ) {
                //AnimatedHeartText()
            }
        }
    }
}

@Composable
fun AnimatedBurgerIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "burger")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Box(
        modifier = Modifier
            .size(85.dp)
            .graphicsLayer { translationY = bounce }
            .background(
                Color(0xFFFF6B35),
                shape = androidx.compose.foundation.shape.CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher),
            contentDescription = "App Logo",
            modifier = Modifier.size(48.dp)
        )
    }
}

//@Composable
//fun AnimatedHeartText() {
//    val infiniteTransition = rememberInfiniteTransition(label = "heart")
//    val heartScale by infiniteTransition.animateFloat(
//        initialValue = 1f,
//        targetValue = 1.2f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(800, easing = FastOutSlowInEasing),
//            repeatMode = RepeatMode.Reverse
//        ),
//        label = "heart_scale"
//    )
//
//    Text(
//        buildAnnotatedString {
//            append("Made with ")
//            withStyle(SpanStyle(color = Color.Red)) {
//                append("❤")
//            }
//            append(" by students")
//        },
//        color = Color(0xFF5B6B7A),
//        fontSize = 16.sp,
//        modifier = Modifier.graphicsLayer {
//            // Only scale the heart emoji (rough approximation)
//            scaleX = if (heartScale > 1.1f) heartScale else 1f
//            scaleY = if (heartScale > 1.1f) heartScale else 1f
//        }
//    )
//}

@Composable
fun FloatingFoodEmojis() {
    val emojis = listOf(
        "🍕", "🍔", "🌮", "🍣", "🍜", "🧀", "🥤", "🍟",
        "🌭", "🥪", "🥙", "🌯", "🍱", "🍛", "🍝", "🍲",
        "🥗", "🍖", "🍗", "🥓", "🥩", "🍳", "🥞", "🧇",
        "🥯", "🍞", "🥖", "🥨", "🥐", "🧈", "🥜", "🍯",
        "🥛", "☕", "🧃", "🥤", "🧋", "🍺", "🍻", "🥂",
        "🍷", "🥃", "🍸", "🍹", "🍾", "🧊", "🍰", "🎂",
        "🧁", "🥧", "🍮", "🍭", "🍬", "🍫", "🍿", "🍩",
        "🍪", "🌰", "🥥", "🥝", "🍓", "🫐", "🍇", "🍈",
        "🍉", "🍊", "🍋", "🍌", "🍍", "🥭", "🍑", "🍒",

    )

    val infiniteTransition = rememberInfiniteTransition(label = "floating")

    Box(modifier = Modifier.fillMaxSize()) {
        // Create a grid covering the entire screen
        val rows = 20
        val cols = 8

        repeat(rows) { row ->
            repeat(cols) { col ->
                val emojiIndex = (row * cols + col) % emojis.size
                val emoji = emojis[emojiIndex]

                // Calculate base position
                val xPosition = (col * 50).dp + (row % 2 * 25).dp
                val yPosition = (row * 60).dp - 100.dp

                // Add some randomness to base positions
                val randomOffsetX = remember { (-20..20).random().dp }
                val randomOffsetY = remember { (-20..20).random().dp }

                // Random size and brightness for each emoji
                val randomSize =  30.sp
//                val randomAlpha = remember { (0.05f..0.20f).random() }

                // Individual floating animations for each emoji
                val floatY by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = (-500..500).random().toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 80000 + (row * col * 10),
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "float_y_${row}_$col"
                )

                val floatX by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = (-5000..5000).random().toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = (80000) + (row * col * 15),
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "float_x_${row}_$col"
                )

                // Gentle rotation
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 100000 + (row * col * 20),
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "rotate_${row}_$col"
                )

                Text(
                    text = emoji,
                    fontSize = randomSize, // Random size between 12sp and 24sp
                    color = Color.Gray.copy(alpha = 0.20f), // Random opacity between 5% and 20%
                    modifier = Modifier
                        .offset(
                            x = xPosition + randomOffsetX,
                            y = yPosition + randomOffsetY
                        )
                        .graphicsLayer {
                            translationY = floatY
                            translationX = floatX
                            rotationZ = rotation * 0.1f
                        }
                )
            }
        }
    }
}



@Composable
fun SignInForm(
    viewModel: AuthViewModel,
    onSwitchToSignUp: () -> Unit,
    uiState: AuthUiState
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .animateContentSize(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .animateContentSize(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        AnimatedVisibility(
            visible = uiState is AuthUiState.Error,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            if (uiState is AuthUiState.Error) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Button(
            onClick = { viewModel.signIn(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 16.dp)
                .animateContentSize(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1563F7)),
            enabled = uiState !is AuthUiState.Loading
        ) {
            AnimatedContent(
                targetState = uiState is AuthUiState.Loading,
                label = "button_content"
            ) { isLoading ->
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Sign In", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Don't have an account? ")
            TextButton(
                onClick = onSwitchToSignUp,
                modifier = Modifier.animateContentSize()
            ) {
                Text("Sign Up", color = Color(0xFF1563F7))
            }
        }
    }
}

@Composable
fun SignUpForm(
    viewModel: AuthViewModel,
    onSwitchToSignIn: () -> Unit,
    uiState: AuthUiState
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .animateContentSize(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .animateContentSize(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .animateContentSize(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .animateContentSize(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        AnimatedVisibility(
            visible = uiState is AuthUiState.Error,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            if (uiState is AuthUiState.Error) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Button(
            onClick = {
                viewModel.signUp(email, password, name, phone, UserRole.CUSTOMER)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 16.dp)
                .animateContentSize(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1563F7)),
            enabled = uiState !is AuthUiState.Loading
        ) {
            AnimatedContent(
                targetState = uiState is AuthUiState.Loading,
                label = "button_content"
            ) { isLoading ->
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Sign Up", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Already have an account? ")
            TextButton(
                onClick = onSwitchToSignIn,
                modifier = Modifier.animateContentSize()
            ) {
                Text("Sign In", color = Color(0xFF1563F7))
            }
        }
    }
}
