function checkTrash(listId) {
    if (document.querySelectorAll(`#${listId} .card`).length != 0) {
        document.querySelector(`#${listId} .trash-button`).hidden = true
    } else {
        document.querySelector(`#${listId} .trash-button`).hidden = false
    }
}

Array.from(document.querySelectorAll(".list")).map(list =>
    checkTrash(list.id)
);

function changeListTitle(listId) {
    document.querySelector(`#${listId} .edit-button input`).hidden = false
    document.querySelectorAll(`#${listId} .edit-button .image-button`)[0].hidden = true
    document.querySelectorAll(`#${listId} .edit-button .image-button`)[1].hidden = false
    document.querySelector(`#${listId} h1`).hidden = true
}

function createTaskObject(taskElement, index) {
    return {
        id: taskElement.id.split("-")[1],
        position: index
    };
}

function getTasksFromList(listId) {
    let listElement = document.getElementById(listId);
    let taskElements = listElement.querySelectorAll('.card');

    const tasks = Array.from(taskElements).map((taskElement, index) =>
        createTaskObject(taskElement, index)
    );

    return tasks;
}

async function saveState(start, end, taskId) {
    if (start === end) {
        let tasks = getTasksFromList(start);
        let listId = start.split("-")[1];
        updatePositions(listId, tasks);
    } else {
        let listFrom = getTasksFromList(start);
        let listTarget = getTasksFromList(end);
        let listFromId = start.split("-")[1];
        let listTargetId = end.split("-")[1];

        checkTrash(start);
        checkTrash(end);

        await moveTask(listTargetId, taskId.split("-")[1]);
        await updatePositions(listFromId, listFrom);
        await updatePositions(listTargetId, listTarget);
    }
}

async function updatePositions(listId, tasks) {
    await fetch(`/lists/${listId}/change-order`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(tasks),
    });
}

async function moveTask(listId, taskId){
    await fetch(`/lists/${listId}/move/${taskId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    });
}