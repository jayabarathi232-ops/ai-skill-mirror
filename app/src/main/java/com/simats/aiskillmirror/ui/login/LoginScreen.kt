package com.simats.aiskillmirror.ui.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.ui.register.CustomOutlinedInputField
import com.simats.aiskillmirror.ui.register.EmailIcon
import com.simats.aiskillmirror.ui.register.EyeIcon
import com.simats.aiskillmirror.ui.register.GoogleIcon
import com.simats.aiskillmirror.ui.register.LockIcon
import com.simats.aiskillmirror.ui.splash.TopBarHeader
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onGoogleSignInClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginUiEvent.NavigateToHome -> onLoginClick()
                is LoginUiEvent.NavigateToSignUp -> onSignUpClick()
                is LoginUiEvent.NavigateToForgotPassword -> onForgotPasswordClick()
                is LoginUiEvent.ContinueWithGoogle -> onGoogleSignInClick()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // Top Bar
            TopBarHeader(appName = "AI Skills Mirror")

            Spacer(modifier = Modifier.height(28.dp))

            // Headline
            Text(
                text = "Welcome back,",
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )
            Text(
                text = "future you.",
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pick up where your progress left off.",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Field 1: Email Address
            Text(
                text = "Email address",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            CustomOutlinedInputField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                placeholder = "Email address",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { EmailIcon(size = 20.dp, color = TextSecondary) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Field 2: Password
            Text(
                text = "Password",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            CustomOutlinedInputField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                placeholder = "Password",
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { LockIcon(size = 20.dp, color = TextSecondary) },
                trailingIcon = {
                    Box(
                        modifier = Modifier.clickable { viewModel.onTogglePasswordVisibility() },
                        contentAlignment = Alignment.Center
                    ) {
                        EyeIcon(size = 20.dp, color = TextSecondary)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Remember Me & Forgot Password Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.isRememberMeChecked,
                        onCheckedChange = { viewModel.onRememberMeChange(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryGradientStart,
                            uncheckedColor = TextSecondary,
                            checkmarkColor = Color.White
                        )
                    )
                    Text(
                        text = "Remember me",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Text(
                    text = "Forgot password?",
                    color = TaglineText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        viewModel.onForgotPasswordClick()
                        onForgotPasswordClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Button: Log in
            Button(
                onClick = {
                    viewModel.onLoginClick()
                    onLoginClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGradientStart),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Canvas(modifier = Modifier.size(16.dp)) {
                            val strokeWidth = 2.dp.toPx()
                            drawPath(
                                path = Path().apply {
                                    moveTo(2.dp.toPx(), 8.dp.toPx())
                                    lineTo(14.dp.toPx(), 8.dp.toPx())
                                    moveTo(9.dp.toPx(), 3.dp.toPx())
                                    lineTo(14.dp.toPx(), 8.dp.toPx())
                                    lineTo(9.dp.toPx(), 13.dp.toPx())
                                },
                                color = Color.White,
                                style = Stroke(width = strokeWidth)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log in",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider: or continue with
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF22293F),
                    thickness = 1.dp
                )
                Text(
                    text = "or continue with",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF22293F),
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Google Social Login Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceDark)
                    .border(1.dp, Color(0xFF2A314A), RoundedCornerShape(14.dp))
                    .clickable {
                        viewModel.onGoogleSignInClick()
                        onGoogleSignInClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    GoogleIcon(size = 20.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continue with Google",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security & Privacy Card Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF161530))
                    .border(1.dp, Color(0xFF2D2852), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF28224D)),
                        contentAlignment = Alignment.Center
                    ) {
                        ShieldCheckIcon(size = 18.dp, color = TaglineText)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Your learning data is private, encrypted, and always yours.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer: New to AI Skills Mirror? Sign up
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = TextSecondary, fontSize = 14.sp)) {
                    append("New to AI Skills Mirror? ")
                }
                withStyle(
                    style = SpanStyle(
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Sign up")
                }
            }

            Text(
                text = annotatedString,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.onSignUpClick()
                        onSignUpClick()
                    }
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ShieldCheckIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Shield path
        val shieldPath = Path().apply {
            moveTo(w * 0.50f, h * 0.12f)
            lineTo(w * 0.85f, h * 0.25f)
            quadraticTo(w * 0.85f, h * 0.65f, w * 0.50f, h * 0.88f)
            quadraticTo(w * 0.15f, h * 0.65f, w * 0.15f, h * 0.25f)
            close()
        }
        drawPath(path = shieldPath, color = color, style = Stroke(width = strokeWidth))

        // Checkmark inside shield
        val checkPath = Path().apply {
            moveTo(w * 0.35f, h * 0.48f)
            lineTo(w * 0.46f, h * 0.60f)
            lineTo(w * 0.65f, h * 0.38f)
        }
        drawPath(path = checkPath, color = color, style = Stroke(width = strokeWidth))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    AISkillsMirrorTheme {
        LoginScreen(
            viewModel = LoginViewModel()
        )
    }
}
