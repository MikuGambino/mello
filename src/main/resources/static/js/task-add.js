var subtaskIndex = 0;

function addSubtask() {
    var container = document.getElementById('subtasks-container');

    var newSubtaskDiv = document.createElement('div');
    newSubtaskDiv.setAttribute('id', 'subtask-' + subtaskIndex);

    var input = document.createElement('input');
    input.type = 'text';
    input.name = 'subtasks[' + subtaskIndex + '].name';
    input.placeholder = 'Название подзадачи';
    input.required = true;

    var deleteButton = document.createElement('button');
    deleteButton.type = 'button';
    deleteButton.textContent = 'Удалить';
    deleteButton.classList.add('button');
    deleteButton.classList.add('red-button');
    deleteButton.onclick = (function(index) {
        return function() {
            removeSubtask(index);
        };
    })(subtaskIndex);

    newSubtaskDiv.appendChild(input);
    newSubtaskDiv.appendChild(deleteButton);
    container.appendChild(newSubtaskDiv);
    subtaskIndex++;
}

function removeSubtask(index) {
    var subtaskDiv = document.getElementById('subtask-' + index);
    if (subtaskDiv) {
        subtaskDiv.parentNode.removeChild(subtaskDiv);
    }
}