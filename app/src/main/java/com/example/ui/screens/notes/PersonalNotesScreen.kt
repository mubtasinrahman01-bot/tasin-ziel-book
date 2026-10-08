package com.example.ui.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.NoteEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.StylizedEmptyStateView
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.BorderDark
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray600
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PersonalNotesScreen(
    notes: List<NoteEntity>,
    allNotes: List<NoteEntity>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onDeleteNote: (NoteEntity) -> Unit,
    onEditNote: (NoteEntity) -> Unit,
    showAddDialog: Boolean,
    editingNote: NoteEntity?,
    onSetShowAddDialog: (Boolean) -> Unit,
    onSaveNote: (String, String, String, Long?) -> Unit,
    onBackToHome: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val categories = listOf("All", "Journal", "Daily Log", "Ideas", "Reflection")

    // State for viewing full note detail in modal/dialog
    var selectedNoteForDetail by remember { mutableStateOf<NoteEntity?>(null) }
    var noteToDeleteConfirm by remember { mutableStateOf<NoteEntity?>(null) }

    val displayedNotes = notes.filter {
        if (selectedCategoryFilter == "All") true else it.category == selectedCategoryFilter
    }

    Box(modifier = modifier.fillMaxSize().background(PureBlack)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("personal_notes_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Sub-page Top Bar with clear Back Button [←]
            if (onBackToHome != null) {
                item {
                    SubPageTopBar(
                        title = "Notes",
                        subtitle = "নোট ও বিস্তারিত সংগ্রহ",
                        iconRes = R.drawable.ic_coil_notebook_pen,
                        onBack = onBackToHome
                    )
                }
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notes_search_input"),
                    placeholder = { Text("Search notes / খুঁজুন...", color = Gray500) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Notes",
                            tint = Gray400
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Search",
                                    tint = Gray400
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // Category filter chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        CategoryChip(
                            text = cat,
                            isSelected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = cat }
                        )
                    }
                }
            }

            // Note count summary indicator
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ALL NOTES (${displayedNotes.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gray500
                        )
                    )
                    Text(
                        text = "Tap a note to view details",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Gray600
                        )
                    )
                }
            }

            // Note cards: clean vertical list
            if (displayedNotes.isEmpty()) {
                item {
                    StylizedEmptyStateView(
                        painter = painterResource(id = R.drawable.ic_coil_notebook_pen),
                        title = if (searchQuery.isNotEmpty()) "No matching notes found" else "No notes logged yet",
                        subtitle = "নোট বা সারসংক্ষেপ যোগ করতে নিচের + বাটনে চাপুন।",
                        actionButtonText = "Write New Note",
                        onActionClick = { onSetShowAddDialog(true) }
                    )
                }
            } else {
                items(displayedNotes, key = { it.id }) { note ->
                    NoteVerticalCard(
                        note = note,
                        onClick = { selectedNoteForDetail = note }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }

        // Floating + Button at bottom-right corner to quickly create a new note
        FloatingActionButton(
            onClick = { onSetShowAddDialog(true) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_note_fab"),
            shape = RoundedCornerShape(12.dp),
            containerColor = PureWhite,
            contentColor = PureBlack
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create New Note",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Modal/Dialog for Full-Screen View Details
    selectedNoteForDetail?.let { currentNote ->
        NoteDetailDialog(
            note = currentNote,
            onDismiss = { selectedNoteForDetail = null },
            onEdit = {
                selectedNoteForDetail = null
                onEditNote(currentNote)
            },
            onDelete = {
                selectedNoteForDetail = null
                noteToDeleteConfirm = currentNote
            }
        )
    }

    // Delete Confirmation Dialog
    noteToDeleteConfirm?.let { noteToDelete ->
        AlertDialog(
            onDismissRequest = { noteToDeleteConfirm = null },
            title = {
                Text(
                    text = "Delete Note / নোট মুছুন",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "\"${noteToDelete.title}\" নোটটি কি মুছে ফেলতে চান?",
                    color = Gray300
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteNote(noteToDelete)
                        noteToDeleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PureWhite,
                        contentColor = PureBlack
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { noteToDeleteConfirm = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = Gray400)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = DarkCard,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Add or Edit Dialog
    if (showAddDialog) {
        AddEditNoteDialog(
            existingNote = editingNote,
            onDismiss = { onSetShowAddDialog(false) },
            onConfirm = { title, content, category ->
                onSaveNote(title, content, category, editingNote?.id)
            }
        )
    }
}

/**
 * Note Card Layout inspired by image_1.png:
 * - Sleek, dark-grey rounded card container against deep black background (#141414 / DarkCard)
 * - Prominent bold title text (e.g., "Kp kc er math", "Gmail account")
 * - Short 1-line muted text preview of the note content underneath title
 * - Created/Modified Date at the bottom of the card (e.g. "September 8", "August 17")
 * - Clicking/tapping opens the full view detail modal
 */
@Composable
fun NoteVerticalCard(
    note: NoteEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Format date like "September 8" or "August 17"
    val dateDisplay = remember(note.updatedAt) {
        SimpleDateFormat("MMMM d", Locale.ENGLISH).format(Date(note.updatedAt))
    }

    // First line preview of content
    val previewText = remember(note.content) {
        val firstLine = note.content.lines().firstOrNull { it.isNotBlank() } ?: note.content
        firstLine.trim()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("note_card_${note.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Optional top category tag (subtle)
            if (note.category.isNotBlank() && note.category != "All") {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(DarkElevated)
                        .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = note.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray400
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Title: Prominent bold title text (e.g., "Kp kc er math", "Gmail account")
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = PureWhite
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Preview Text: A short 1-line muted text preview of the note content underneath title
            Text(
                text = if (previewText.isNotBlank()) previewText else "No additional text",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    color = Gray500
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Created/Modified Date: Displayed at bottom of card (e.g., "September 8", "August 17")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateDisplay,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = Gray400,
                        fontWeight = FontWeight.Normal
                    )
                )

                // Sleek subtle notebook indicator icon
                Icon(
                    painter = painterResource(id = R.drawable.ic_coil_notebook_pen),
                    contentDescription = null,
                    tint = Gray600,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Full-screen or modal dialog showing the full note content, title, date,
 * and an Edit & Delete option.
 */
@Composable
fun NoteDetailDialog(
    note: NoteEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val fullDateDisplay = remember(note.updatedAt) {
        SimpleDateFormat("MMMM d, yyyy • h:mm a", Locale.ENGLISH).format(Date(note.updatedAt))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Action Bar in Modal: Category + Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkElevated)
                            .border(1.dp, BorderDark, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = note.category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PureWhite
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("note_detail_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = PureWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Full Title
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Date
                    Text(
                        text = fullDateDisplay,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Gray400,
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(BorderDark)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Full Content (Bengali & English support)
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Gray300,
                            lineHeight = 24.sp,
                            fontSize = 15.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Row: Edit & Delete buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("note_detail_delete"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PureWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Gray400,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete", color = PureWhite, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onEdit,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("note_detail_edit"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PureWhite,
                            contentColor = PureBlack
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = PureBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit", color = PureBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditNoteDialog(
    existingNote: NoteEntity?,
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf(existingNote?.title ?: "") }
    var content by remember { mutableStateOf(existingNote?.content ?: "") }
    var category by remember { mutableStateOf(existingNote?.category ?: "Journal") }

    val categories = listOf("Journal", "Daily Log", "Ideas", "Reflection")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingNote != null) "Edit Note / নোট সম্পাদনা" else "New Note / নতুন নোট",
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title / শিরোনাম", color = Gray400) },
                    placeholder = { Text("e.g. Kp kc er math / Gmail account", color = Gray500) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_note_input_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                // Category selector
                Column {
                    Text(
                        text = "Category / বিভাগ",
                        style = MaterialTheme.typography.labelMedium.copy(color = Gray400)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            CategoryChip(
                                text = cat,
                                isSelected = category == cat,
                                onClick = { category = cat }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content / বিস্তারিত বিবরণ", color = Gray400) },
                    placeholder = { Text("Write your thoughts, details or formula...", color = Gray500) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .testTag("add_note_input_content"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 10
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onConfirm(title.trim(), content.trim(), category)
                    }
                },
                enabled = title.isNotBlank() && content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureWhite,
                    contentColor = PureBlack
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("add_note_confirm_button")
            ) {
                Text(
                    text = if (existingNote != null) "Update Note" else "Save Note",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Gray400)
            ) {
                Text("Cancel")
            }
        },
        containerColor = DarkCard,
        shape = RoundedCornerShape(12.dp)
    )
}
