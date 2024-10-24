
function updateSubtaskStatus(id, taskId, subtaskId, completed) {
    fetch(`/projects/${id}/tasks/${taskId}/subtasks/${subtaskId}/update-status?status=${completed}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
    });
}