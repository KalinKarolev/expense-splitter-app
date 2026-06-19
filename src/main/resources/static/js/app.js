const groupsList = document.getElementById('groupsList');
const groupsEmpty = document.getElementById('groupsEmpty');
const formMessage = document.getElementById('formMessage');
let formMessageTimeoutId = null;

async function loadGroups() {
    try {
        const response = await fetch('/api/groups');

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        const groups = await response.json();
        renderGroups(groups);
    } catch (error) {
        showMessage('Could not load groups. Please try again.', true);
        console.error(error);
    }
}

function renderGroups(groups) {
    groupsList.innerHTML = '';

    if (!groups || groups.length === 0) {
        groupsEmpty.style.display = 'flex';
        return;
    }

    groupsEmpty.style.display = 'none';

    groups.forEach(group => {
        const item = document.createElement('li');
        const button = document.createElement('button');
        button.className = 'group-card';
        button.type = 'button';
        button.dataset.groupId = group.id;

        const title = document.createElement('h3');
        title.textContent = group.name;

        const description = document.createElement('p');
        description.textContent = group.description || 'No description added yet.';

        const meta = document.createElement('span');
        meta.className = 'group-meta';
        meta.textContent = `Open group #${group.id}`;

        button.append(title, description, meta);
        item.appendChild(button);
        groupsList.appendChild(item);
    });
}

function showMessage(message, isError = false) {
    clearMessageTimer();
    formMessage.textContent = message;
    formMessage.classList.toggle('error', isError);
}

function showTimedMessage(message) {
    showMessage(message);
    formMessageTimeoutId = window.setTimeout(() => {
        formMessage.textContent = '';
        formMessageTimeoutId = null;
    }, 3000);
}

function clearMessageTimer() {
    if (formMessageTimeoutId) {
        window.clearTimeout(formMessageTimeoutId);
        formMessageTimeoutId = null;
    }
}

function openGroup(groupId) {
    window.location.href = `/groups/${groupId}`;
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('createForm');
    const submitButton = form.querySelector('button[type="submit"]');

    loadGroups();

    groupsList.addEventListener('click', event => {
        const card = event.target.closest('.group-card');

        if (card) {
            openGroup(card.dataset.groupId);
        }
    });

    form.addEventListener('submit', async event => {
        event.preventDefault();
        showMessage('');

        const name = document.getElementById('name').value.trim();
        const description = document.getElementById('description').value.trim();

        if (!name) {
            showMessage('Group name is required.', true);
            return;
        }

        submitButton.disabled = true;

        try {
            const response = await fetch('/api/groups', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({name, description})
            });

            if (!response.ok) {
                throw new Error(await response.text());
            }

            form.reset();
            showTimedMessage('Group created successfully.');
            await loadGroups();
        } catch (error) {
            showMessage('Could not create the group. Please check the form and try again.', true);
            console.error(error);
        } finally {
            submitButton.disabled = false;
        }
    });
});
