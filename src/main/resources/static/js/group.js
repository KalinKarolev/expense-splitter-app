const groupTitle = document.getElementById('groupTitle');
const groupDescription = document.getElementById('groupDescription');
const memberForm = document.getElementById('memberForm');
const memberNameInput = document.getElementById('memberName');
const addMemberButton = document.getElementById('addMemberBtn');
const memberMessage = document.getElementById('memberMessage');
const membersEmpty = document.getElementById('membersEmpty');
const membersList = document.getElementById('membersList');

let memberMessageTimeoutId = null;

function getGroupIdFromPath() {
    const pathParts = window.location.pathname.split('/').filter(Boolean);
    const groupId = Number(pathParts[pathParts.length - 1]);
    return Number.isInteger(groupId) && groupId > 0 ? groupId : null;
}

function showMemberMessage(message, isError = false) {
    clearMemberMessageTimer();
    memberMessage.textContent = message;
    memberMessage.classList.toggle('error', isError);
}

function showTimedMemberMessage(message) {
    showMemberMessage(message);
    memberMessageTimeoutId = window.setTimeout(() => {
        memberMessage.textContent = '';
        memberMessageTimeoutId = null;
    }, 3000);
}

function clearMemberMessageTimer() {
    if (memberMessageTimeoutId) {
        window.clearTimeout(memberMessageTimeoutId);
        memberMessageTimeoutId = null;
    }
}

async function loadGroup(groupId) {
    try {
        const response = await fetch(`/api/groups/${groupId}`);

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        const group = await response.json();
        groupTitle.textContent = group.name;
        groupDescription.textContent = group.description || 'No description added yet.';
        memberNameInput.disabled = false;
        addMemberButton.disabled = false;
    } catch (error) {
        groupTitle.textContent = 'Group not found';
        groupDescription.textContent = 'Return to the groups page and select an existing group.';
        showMemberMessage('Could not load this group.', true);
        console.error(error);
    }
}

async function loadMembers(groupId) {
    try {
        const response = await fetch(`/api/groups/${groupId}/members`);

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        const members = await response.json();
        renderMembers(members);
    } catch (error) {
        showMemberMessage('Could not load members for this group.', true);
        console.error(error);
    }
}

function renderMembers(members) {
    membersList.innerHTML = '';

    if (!members || members.length === 0) {
        membersEmpty.textContent = 'No members added to this group yet.';
        membersEmpty.style.display = 'block';
        return;
    }

    membersEmpty.style.display = 'none';

    members.forEach((member, index) => {
        const item = document.createElement('li');
        item.className = 'member-card';
        item.dataset.memberId = member.id;

        const name = document.createElement('span');
        name.textContent = member.name;

        const meta = document.createElement('small');
        meta.textContent = `Member #${index + 1}`;

        const details = document.createElement('div');
        details.className = 'member-card-details';
        details.append(name, meta);

        const deleteButton = document.createElement('button');
        deleteButton.className = 'delete-member-button';
        deleteButton.type = 'button';
        deleteButton.dataset.memberId = member.id;
        deleteButton.setAttribute('aria-label', `Remove ${member.name}`);
        deleteButton.innerHTML = '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/><path d="M10 11v5"/><path d="M14 11v5"/></svg>';

        item.append(details, deleteButton);
        membersList.appendChild(item);
    });
}

async function addMember(groupId) {
    showMemberMessage('');

    const name = memberNameInput.value.trim();

    if (!name) {
        showMemberMessage('Member name is required.', true);
        return;
    }

    addMemberButton.disabled = true;

    try {
        const response = await fetch(`/api/groups/${groupId}/members`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({name})
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        memberForm.reset();
        showTimedMemberMessage('Member added successfully.');
        await loadMembers(groupId);
    } catch (error) {
        showMemberMessage('Could not add the member. Please check the form and try again.', true);
        console.error(error);
    } finally {
        addMemberButton.disabled = false;
    }
}

async function deleteMember(groupId, memberId) {
    try {
        const response = await fetch(`/api/groups/${groupId}/members/${memberId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        showMemberMessage('');
        await loadMembers(groupId);
    } catch (error) {
        showMemberMessage('Could not remove the member. Please try again.', true);
        console.error(error);
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    const groupId = getGroupIdFromPath();

    if (!groupId) {
        groupTitle.textContent = 'Group not found';
        groupDescription.textContent = 'Return to the groups page and select an existing group.';
        showMemberMessage('Invalid group URL.', true);
        return;
    }

    await loadGroup(groupId);
    await loadMembers(groupId);

    memberForm.addEventListener('submit', event => {
        event.preventDefault();
        addMember(groupId);
    });

    membersList.addEventListener('click', event => {
        const deleteButton = event.target.closest('.delete-member-button');

        if (deleteButton) {
            deleteMember(groupId, Number(deleteButton.dataset.memberId));
        }
    });
});
