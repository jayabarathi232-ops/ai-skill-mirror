package com.simats.aiskillmirror.ui.register

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
import com.simats.aiskillmirror.ui.splash.TopBarHeader
import com.simats.aiskillmirror.ui.theme.*

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onCreateAccountClick: () -> Unit = {},
    onGoogleSignInClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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

            Spacer(modifier = Modifier.height(24.dp))

            // Headline
            Text(
                text = "Your growth",
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )
            Text(
                text = "starts here.",
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create an account to discover your potential.",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Field 1: Full Name
            Text(
                text = "Full name",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            CustomOutlinedInputField(
                value = uiState.fullName,
                onValueChange = { viewModel.onFullNameChange(it) },
                placeholder = "Full name",
                leadingIcon = { PersonIcon(size = 20.dp, color = TextSecondary) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Field 2: Email Address
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

            // Field 3: Password
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

            Spacer(modifier = Modifier.height(8.dp))

            // Password Strength Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                CheckCircleIcon(size = 16.dp, color = GreenNode)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = uiState.passwordStrengthText,
                    color = GreenNode,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms Agreement Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = uiState.isTermsAccepted,
                    onCheckedChange = { viewModel.onTermsAcceptedChange(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryGradientStart,
                        uncheckedColor = TextSecondary,
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = TextSecondary, fontSize = 12.sp)) {
                            append("I agree to the ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("Terms of Service")
                        }
                        withStyle(style = SpanStyle(color = TextSecondary, fontSize = 12.sp)) {
                            append(" and ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("Privacy Policy.")
                        }
                    },
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Button: Create account
            Button(
                onClick = {
                    viewModel.onCreateAccountClick()
                    onCreateAccountClick()
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
                            text = "Create account",
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

            Spacer(modifier = Modifier.height(24.dp))

            // Footer: Already have an account? Log in
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = TextSecondary, fontSize = 14.sp)) {
                    append("Already have an account? ")
                }
                withStyle(
                    style = SpanStyle(
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Log in")
                }
            }

            Text(
                text = annotatedString,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.onLoginClick()
                        onLoginClick()
                    }
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CustomOutlinedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable () -> Unit,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFF232A42), RoundedCornerShape(14.dp)),
        placeholder = { Text(text = placeholder, color = TextSecondary, fontSize = 14.sp) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark,
            disabledContainerColor = SurfaceDark,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun PersonIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Head circle
        drawCircle(
            color = color,
            radius = w * 0.22f,
            center = Offset(w / 2, h * 0.32f),
            style = Stroke(width = strokeWidth)
        )

        // Body arc
        drawPath(
            path = Path().apply {
                moveTo(w * 0.20f, h * 0.85f)
                quadraticTo(w * 0.20f, h * 0.60f, w * 0.50f, h * 0.60f)
                quadraticTo(w * 0.80f, h * 0.60f, w * 0.80f, h * 0.85f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun EmailIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Envelope rectangle
        drawPath(
            path = Path().apply {
                moveTo(w * 0.15f, h * 0.25f)
                lineTo(w * 0.85f, h * 0.25f)
                lineTo(w * 0.85f, h * 0.75f)
                lineTo(w * 0.15f, h * 0.75f)
                close()

                // Flap lines
                moveTo(w * 0.15f, h * 0.25f)
                lineTo(w * 0.50f, h * 0.52f)
                lineTo(w * 0.85f, h * 0.25f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun LockIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Lock shackle arc
        drawPath(
            path = Path().apply {
                moveTo(w * 0.30f, h * 0.45f)
                lineTo(w * 0.30f, h * 0.30f)
                quadraticTo(w * 0.30f, h * 0.15f, w * 0.50f, h * 0.15f)
                quadraticTo(w * 0.70f, h * 0.15f, w * 0.70f, h * 0.30f)
                lineTo(w * 0.70f, h * 0.45f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )

        // Lock body box
        drawPath(
            path = Path().apply {
                moveTo(w * 0.22f, h * 0.45f)
                lineTo(w * 0.78f, h * 0.45f)
                lineTo(w * 0.78f, h * 0.85f)
                lineTo(w * 0.22f, h * 0.85f)
                close()
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun EyeIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Eye shape
        drawPath(
            path = Path().apply {
                moveTo(w * 0.15f, h * 0.50f)
                quadraticTo(w * 0.50f, h * 0.20f, w * 0.85f, h * 0.50f)
                quadraticTo(w * 0.50f, h * 0.80f, w * 0.15f, h * 0.50f)
                close()
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )

        // Pupil
        drawCircle(
            color = color,
            radius = w * 0.12f,
            center = Offset(w / 2, h / 2)
        )
    }
}

@Composable
fun CheckCircleIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 1.8.dp.toPx()

        // Outer circle
        drawCircle(
            color = color,
            radius = w * 0.45f,
            center = Offset(w / 2, h / 2),
            style = Stroke(width = strokeWidth)
        )

        // Checkmark
        drawPath(
            path = Path().apply {
                moveTo(w * 0.28f, h * 0.50f)
                lineTo(w * 0.43f, h * 0.65f)
                lineTo(w * 0.72f, h * 0.35f)
            },
            color = color,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
fun GoogleIcon(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = 2.dp.toPx()

        // Stylized G logo outline
        drawPath(
            path = Path().apply {
                moveTo(w * 0.85f, h * 0.50f)
                lineTo(w * 0.50f, h * 0.50f)
                moveTo(w * 0.82f, h * 0.35f)
                quadraticTo(w * 0.50f, h * 0.15f, w * 0.25f, h * 0.35f)
                quadraticTo(w * 0.10f, h * 0.50f, w * 0.25f, h * 0.70f)
                quadraticTo(w * 0.50f, h * 0.88f, w * 0.82f, h * 0.65f)
            },
            color = TextPrimary,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    AISkillsMirrorTheme {
        RegisterScreen(
            viewModel = RegisterViewModel()
        )
    }
}
