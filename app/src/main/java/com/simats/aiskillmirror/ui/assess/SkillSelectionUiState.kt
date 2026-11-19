package com.simats.aiskillmirror.ui.assess

import com.simats.aiskillmirror.data.network.SubcategoryItemData

enum class SkillIconType {
    MUSIC,
    PHOTOGRAPHY,
    DRAWING,
    TECHNICAL,
    COMMUNICATION,
    PROBLEM_SOLVING,
    INTERVIEW
}

data class SkillSubcategory(
    val id: String,
    val name: String,
    val mainSkillId: String,
    val tagline: String,
    val description: String,
    val iconEmoji: String,
    val dailyPracticeTitle: String,
    val durationText: String = "10 min",
    val xpText: String = "+35 XP",
    val beginnerExercise: String,
    val challenge: String,
    val aiTip: String
)

data class SkillCategory(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val lastScore: Int,
    val maxScore: Int = 100,
    val iconType: SkillIconType,
    val iconEmoji: String,
    val subcategories: List<SkillSubcategory>
)

data class SkillSelectionUiState(
    val headerTitle: String = "My Skills",
    val headerCategory: String = "SKILL CATEGORIES & SUBCATEGORIES",
    val tagline: String = "AI-POWERED PERSONALIZED TRAINING",
    val headline: String = "What will you master today?",
    val description: String = "Select an instrument, style, or language for dedicated AI-guided training.",
    val selectedSkillId: String = "music",
    val selectedSubcategory: SkillSubcategory? = null,
    val selectedProficiencyLevel: String = "Beginner",
    val selectedLearningGoal: String = "Master Core Fundamentals",
    val availableGoals: List<String> = listOf(
        "Master Core Fundamentals",
        "Daily Practice & Fluency",
        "Performance & Project Readiness",
        "Portfolio & Creative Showcase"
    ),
    val activeSavedSubcategory: SubcategoryItemData? = null,
    val savedSubcategories: List<SubcategoryItemData> = emptyList(),
    val isSubcategoryDialogOpen: Boolean = false,
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val skills: List<SkillCategory> = defaultSkillCategoriesCatalog()
)

sealed interface SkillSelectionUiEvent {
    data class StartTrainingWithSubcategory(
        val mainSkill: String,
        val subcategory: SkillSubcategory,
        val level: String,
        val goal: String
    ) : SkillSelectionUiEvent
    object StartAssessment : SkillSelectionUiEvent
    object NavigateBack : SkillSelectionUiEvent
}

fun defaultSkillCategoriesCatalog(): List<SkillCategory> = listOf(
    // 1. MUSIC
    SkillCategory(
        id = "music",
        name = "Music",
        subtitle = "Musical Instruments",
        description = "Acoustic & digital instruments, vocal technique, rhythm & melody",
        lastScore = 88,
        iconType = SkillIconType.MUSIC,
        iconEmoji = "🎵",
        subcategories = listOf(
            SkillSubcategory(
                id = "guitar",
                name = "Guitar",
                mainSkillId = "music",
                tagline = "Acoustic & Electric Fretboard",
                description = "Chord transitions, fretboard navigation, fingerpicking & rhythm strumming.",
                iconEmoji = "🎸",
                dailyPracticeTitle = "Daily Guitar Practice",
                durationText = "10 min",
                xpText = "+35 XP",
                beginnerExercise = "Open String Alternate Picking & Chromatic Crawl",
                challenge = "60-Second Strum Sprint: 40 clean chord switches without mute",
                aiTip = "Press right behind the fret wire for minimum finger fatigue and zero fret buzz."
            ),
            SkillSubcategory(
                id = "piano",
                name = "Piano / Keyboard",
                mainSkillId = "music",
                tagline = "Keys, Scales & Chords",
                description = "Key recognition, scales, hand coordination, chord voicings & classical/modern melodies.",
                iconEmoji = "🎹",
                dailyPracticeTitle = "Keyboard Coordination Drill",
                durationText = "10 min",
                xpText = "+35 XP",
                beginnerExercise = "Hanon Finger Independence & C Major Scale",
                challenge = "Two-Hand Counterpoint: Play scale right while left plays contrary motion",
                aiTip = "Keep fingers curved like gently holding a tennis ball. Strike keys with pads."
            ),
            SkillSubcategory(
                id = "violin",
                name = "Violin",
                mainSkillId = "music",
                tagline = "Bowing & Intonation",
                description = "Bowing technique, fingerboard pitch accuracy, posture, intonation & vibrato.",
                iconEmoji = "🎻",
                dailyPracticeTitle = "Bowing & Pitch Accuracy",
                durationText = "12 min",
                xpText = "+35 XP",
                beginnerExercise = "Straight Bowing in Mirror & Drone Pitch Matching",
                challenge = "Bow Division: 4 equal beats per full bow stroke without pressure drops",
                aiTip = "Bow speed and arm weight create beautiful tone, not downward hand pressing."
            ),
            SkillSubcategory(
                id = "drums",
                name = "Drums",
                mainSkillId = "music",
                tagline = "Rudiments & Limb Independence",
                description = "Rudiments, limb independence, dynamic tempo control, backbeats & groove synchronization.",
                iconEmoji = "🥁",
                dailyPracticeTitle = "Drum Rudiments & Pacing",
                durationText = "10 min",
                xpText = "+35 XP",
                beginnerExercise = "Single & Double Stroke Rolls at 80 BPM",
                challenge = "Paradiddle Stamina: 2 minutes of continuous RLRR LRLL without tempo drift",
                aiTip = "Let the drumstick bounce off the head naturally using wrist pivot."
            ),
            SkillSubcategory(
                id = "flute",
                name = "Flute",
                mainSkillId = "music",
                tagline = "Embouchure & Airflow",
                description = "Embouchure control, steady breath support, fingering transitions & melodic articulation.",
                iconEmoji = "🪈",
                dailyPracticeTitle = "Embouchure & Air Flow",
                durationText = "10 min",
                xpText = "+35 XP",
                beginnerExercise = "Long Tone Warmup on Middle B-A-G",
                challenge = "12-Second Sustained Tone within 5 cents across full measure",
                aiTip = "Blow across the embouchure hole like a glass bottle edge, not directly inside."
            ),
            SkillSubcategory(
                id = "veena",
                name = "Veena",
                mainSkillId = "music",
                tagline = "Carnatic Tala & Gamakas",
                description = "Meend glides, gamaka oscillations, thalam coordination, plucking & raga exploration.",
                iconEmoji = "🪕",
                dailyPracticeTitle = "Veena Tala & Gamaka",
                durationText = "12 min",
                xpText = "+35 XP",
                beginnerExercise = "Meetu Plucking Sync & Sarali Varisai in 1st speed",
                challenge = "3-Speed Sarali Varisai in Mayamalavagowla with strict Tala sync",
                aiTip = "Keep left hand finger pressure just firm enough to ring clearly against brass frets."
            ),
            SkillSubcategory(
                id = "singing",
                name = "Singing / Vocal Training",
                mainSkillId = "music",
                tagline = "Pitch, Resonance & Breath",
                description = "Vocal warmups, pitch accuracy, breathing diaphragmatic support, resonance & ear training.",
                iconEmoji = "🎤",
                dailyPracticeTitle = "Vocal Warmup & Range",
                durationText = "10 min",
                xpText = "+35 XP",
                beginnerExercise = "Lip Trills, Tongue Hums & Vowel Scale Glides",
                challenge = "Octave Arpeggio: Clean vocal attack on 8 notes without throat tension",
                aiTip = "Drop your jaw gently and keep soft palate raised as if beginning a yawn."
            )
        )
    ),

    // 2. PHOTOGRAPHY
    SkillCategory(
        id = "photo",
        name = "Photography",
        subtitle = "Photography Categories",
        description = "Composition, lighting, framing, lens optics & visual storytelling",
        lastScore = 75,
        iconType = SkillIconType.PHOTOGRAPHY,
        iconEmoji = "📷",
        subcategories = listOf(
            SkillSubcategory(
                id = "portrait_photo",
                name = "Portrait Photography",
                mainSkillId = "photo",
                tagline = "Facial Lighting & Expression",
                description = "Facial lighting, depth of field, natural expression capture & background bokeh.",
                iconEmoji = "👤",
                dailyPracticeTitle = "Portrait Composition Drill",
                durationText = "12 min",
                xpText = "+35 XP",
                beginnerExercise = "Catchlight Finder & 45-Degree Light Angle",
                challenge = "Golden Hour Natural Portrait: Backlit subject with creamy bokeh separation",
                aiTip = "Create distance between subject and background to get creamy, distraction-free bokeh."
            ),
            SkillSubcategory(
                id = "nature_photo",
                name = "Nature Photography",
                mainSkillId = "photo",
                tagline = "Flora, Textures & Ambient Light",
                description = "Flora, macro textures, golden hour natural lighting & composition.",
                iconEmoji = "🌿",
                dailyPracticeTitle = "Nature Texture & Lighting",
                durationText = "10 min",
                xpText = "+35 XP",
                beginnerExercise = "Macro Bark & Leaf Vein Focus Tracking",
                challenge = "Backlit Petal Glow: Capture translucent leaf glow during morning light",
                aiTip = "Overcast cloudy days are nature's giant softbox! Diffused light captures saturated greens."
            ),
            SkillSubcategory(
                id = "landscape_photo",
                name = "Landscape Photography",
                mainSkillId = "photo",
                tagline = "Leading Lines & Horizon Balance",
                description = "Wide-angle framing, leading lines, horizon balance, hyperfocal distance & filters.",
                iconEmoji = "🏔️",
                dailyPracticeTitle = "Leading Lines Landscape",
                durationText = "12 min",
                xpText = "+35 XP",
                beginnerExercise = "Horizon Line Grid Placement & Foreground Anchor",
                challenge = "Layered Mountain Depth: Frame 3 distinct depth planes from foreground to peak",
                aiTip = "A great landscape needs a strong foreground element to draw the viewer's eye into the scene."
            ),
            SkillSubcategory(
                id = "street_photo",
                name = "Street Photography",
                mainSkillId = "photo",
                tagline = "The Decisive Moment & Shadows",
                description = "Decisive moments, candid framing, urban shadows, geometry & street storytelling.",
                iconEmoji = "🚶",
                dailyPracticeTitle = "Decisive Street Moment",
                durationText = "10 min",
                xpText = "+35 XP",
                beginnerExercise = "Shadow Silhouettes & High Contrast Lighting",
                challenge = "Urban Geometry: Find an architectural triangle framing a passing subject",
                aiTip = "Find the light and frame first, then wait patiently for the subject to step into it."
            ),
            SkillSubcategory(
                id = "product_photo",
                name = "Product Photography",
                mainSkillId = "photo",
                tagline = "Tabletop Studio & Reflection Control",
                description = "Studio tabletop lighting, reflective surface control, crisp focus & commercial styling.",
                iconEmoji = "📦",
                dailyPracticeTitle = "Product Tabletop Studio",
                durationText = "12 min",
                xpText = "+35 XP",
                beginnerExercise = "Specular Highlight Placement with Foam Board Reflector",
                challenge = "Commercial Glassware: Capture beverage bottle with clean backlit edge definition",
                aiTip = "Use inexpensive white cardboards as bounce cards; they fill shadows with soft, even light."
            ),
            SkillSubcategory(
                id = "mobile_photo",
                name = "Mobile Photography",
                mainSkillId = "photo",
                tagline = "Smartphone Sensor Optimization",
                description = "Smartphone sensor optimization, tap exposure, grid composition & mobile post-processing.",
                iconEmoji = "📱",
                dailyPracticeTitle = "Mobile Exposure Mastery",
                durationText = "10 min",
                xpText = "+35 XP",
                beginnerExercise = "Tap-to-Expose & Slide Down Exposure Slider",
                challenge = "Inverted Puddle Reflection: Position phone lens 1 inch off ground over water",
                aiTip = "Slightly underexposing mobile photos prevents blown-out highlights that cannot be recovered."
            ),
            SkillSubcategory(
                id = "wildlife_photo",
                name = "Wildlife Photography",
                mainSkillId = "photo",
                tagline = "Shutter Speed & Subject Tracking",
                description = "Fast shutter speeds, subject tracking, animal behavior anticipation & telephoto stability.",
                iconEmoji = "🦅",
                dailyPracticeTitle = "Fast Action Tracking",
                durationText = "12 min",
                xpText = "+35 XP",
                beginnerExercise = "Moving Pet Panning & Continuous Autofocus Burst",
                challenge = "Action Freeze: Capture a bird wings spread in mid-flight at 1/1000s",
                aiTip = "Always leave negative space in front of the animal's gaze so the composition feels open."
            )
        )
    ),

    // 3. CODING
    SkillCategory(
        id = "coding",
        name = "Coding",
        subtitle = "Programming Languages",
        description = "Programming languages, algorithms, data structures & software development",
        lastScore = 92,
        iconType = SkillIconType.TECHNICAL,
        iconEmoji = "💻",
        subcategories = listOf(
            SkillSubcategory(
                id = "python",
                name = "Python",
                mainSkillId = "coding",
                tagline = "Data Structures & AI Scripts",
                description = "Data structures, list comprehensions, OOP, scripts, automation & AI fundamentals.",
                iconEmoji = "🐍",
                dailyPracticeTitle = "Python Logic & Hash Maps",
                durationText = "12 min",
                xpText = "+35 XP",
                beginnerExercise = "List Comprehensions & Dictionary .get() Logic",
                challenge = "Two Sum Hash Map: Find matching indices in O(N) time complexity",
                aiTip = "Prefer list comprehensions and generators over manual loop appends for cleaner, faster code."
            ),
            SkillSubcategory(
                id = "java",
                name = "Java",
                mainSkillId = "coding",
                tagline = "Enterprise OOP & JVM Architecture",
                description = "Class architecture, interfaces, collections framework, multithreading & JVM memory.",
                iconEmoji = "☕",
                dailyPracticeTitle = "Java OOP & Stream Pipeline",
                durationText = "12 min",
                xpText = "+35 XP",
                beginnerExercise = "Interface Implementation & Stream API Filtering",
                challenge = "Thread-Safe Atomic Counter: Synchronized multi-thread increment logic",
                aiTip = "Always program to an interface (List<T>) rather than concrete class (ArrayList<T>)."
            ),
            SkillSubcategory(
                id = "c_prog",
                name = "C Programming",
                mainSkillId = "coding",
                tagline = "Pointers & Memory Architecture",
                description = "Pointers, memory management, structs, low-level I/O & procedural logic.",
                iconEmoji = "⚙️",
                dailyPracticeTitle = "C Pointer Arithmetic",
                durationText = "12 min",
                xpText = "+35 XP",
                beginnerExercise = "Pointer Dereferencing & Safe malloc() Checks",
                challenge = "Singly Linked List: Implement insert_node and delete_node in pure C",
                aiTip = "Always set your pointer to NULL immediately after calling free() to avoid dangling pointers."
            ),
            SkillSubcategory(
                id = "cpp",
                name = "C++",
                mainSkillId = "coding",
                tagline = "Modern C++20 & STL Performance",
                description = "STL containers, pointers/references, modern C++20 features & high-performance design.",
                iconEmoji = "⚡",
                dailyPracticeTitle = "Modern C++ STL & RAII",
                durationText = "12 min",
                xpText = "+35 XP",
                beginnerExercise = "std::vector Lambda Sort & std::make_unique",
                challenge = "Custom Vector Template: Implement dynamic buffer with push_back and reserve",
                aiTip = "Embrace RAII and smart pointers (unique_ptr, shared_ptr) to eliminate manual 'delete'."
            ),
            SkillSubcategory(
                id = "javascript",
                name = "JavaScript",
                mainSkillId = "coding",
                tagline = "Async/Await, DOM & Event Loop",
                description = "Async/await, DOM manipulation, promises, event loop & modern ES6+ paradigms.",
                iconEmoji = "🌐",
                dailyPracticeTitle = "Async/Await & Event Loop",
                durationText = "10 min",
                xpText = "+35 XP",
                beginnerExercise = "Array Reduce & Promise.all Concurrent Execution",
                challenge = "Debounce Function: Write debounce wrapper delaying execution 300ms",
                aiTip = "Async functions always return a Promise; always handle rejections with try/catch."
            ),
            SkillSubcategory(
                id = "html_css",
                name = "HTML & CSS",
                mainSkillId = "coding",
                tagline = "Responsive Flexbox & CSS Grid",
                description = "Semantic markup, flexbox, CSS grid, responsive layouts & modern styling animations.",
                iconEmoji = "🎨",
                dailyPracticeTitle = "Flexbox & Grid Layouts",
                durationText = "10 min",
                xpText = "+35 XP",
                beginnerExercise = "Centering Elements in 3 Ways & 12-Column Grid",
                challenge = "Mobile-First Hamburger Nav: Smooth expandable drawer with pure CSS",
                aiTip = "Design for a 375px mobile screen first! Adding desktop styles via min-width is much cleaner."
            ),
            SkillSubcategory(
                id = "php",
                name = "PHP",
                mainSkillId = "coding",
                tagline = "REST APIs & Prepared Statements",
                description = "Server-side routing, MySQL PDO/mysqli, session handling, REST APIs & backend architecture.",
                iconEmoji = "🐘",
                dailyPracticeTitle = "Secure PHP API Routing",
                durationText = "12 min",
                xpText = "+35 XP",
                beginnerExercise = "PDO Parameter Binding & JSON Output Formatting",
                challenge = "Secure Auth Controller: Implement password_hash() and JWT session handling",
                aiTip = "Never concatenate raw variables into SQL queries! Prepared statements eliminate injection risk."
            )
        )
    )
)
