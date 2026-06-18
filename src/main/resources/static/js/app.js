const groupsList = document.getElementById('groupsList');
const groupsEmpty = document.getElementById('groupsEmpty');
const groupsContent = document.querySelector('.groups-content');
const formMessage = document.getElementById('formMessage');
const groupDetails = document.getElementById('groupDetails');
const selectedGroupText = document.getElementById('selectedGroupText');
const memberForm = document.getElementById('memberForm');
const memberNameInput = document.getElementById('memberName');
const addMemberButton = document.getElementById('addMemberBtn');
const memberMessage = document.getElementById('memberMessage');
const membersEmpty = document.getElementById('membersEmpty');
const membersList = document.getElementById('membersList');

let selectedGroup = null;
let loadedGroups = [];
let memberMessageTimeoutId = null;

async function loadGroups() {
    try {
        const response = await fetch('/api/groups');

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        const groups = await response.json();
        loadedGroups = groups;
        renderGroups(groups);

        if (selectedGroup) {
            const currentGroup = groups.find(group => group.id === selectedGroup.id);
            if (currentGroup) {
                await selectGroup(currentGroup);
            } else {
                clearSelectedGroup();
            }
        }
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

        if (selectedGroup && selectedGroup.id === group.id) {
            button.classList.add('selected');
        }

        const title = document.createElement('h3');
        title.textContent = group.name;

        const description = document.createElement('p');
        description.textContent = group.description || 'No description added yet.';

        const meta = document.createElement('span');
        meta.className = 'group-meta';
        meta.textContent = `Group #${group.id}`;

        button.append(title, description, meta);
        item.appendChild(button);
        groupsList.appendChild(item);
    });
}

function showMessage(message, isError = false) {
    formMessage.textContent = message;
    formMessage.classList.toggle('error', isError);
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

async function selectGroup(group) {
    selectedGroup = group;
    groupDetails.hidden = false;
    groupsContent.classList.add('has-selection');
    selectedGroupText.textContent = `${group.name} - ${group.description || 'No description added yet.'}`;
    memberNameInput.disabled = false;
    addMemberButton.disabled = false;
    showMemberMessage('');
    renderSelectedGroupInList();
    await loadMembers(group.id);
}

function clearSelectedGroup() {
    selectedGroup = null;
    groupDetails.hidden = true;
    groupsContent.classList.remove('has-selection');
    selectedGroupText.textContent = 'Select a group to add and view members.';
    memberForm.reset();
    memberNameInput.disabled = true;
    addMemberButton.disabled = true;
    membersList.innerHTML = '';
    membersEmpty.textContent = 'No group selected.';
    membersEmpty.style.display = 'block';
    showMemberMessage('');
    renderSelectedGroupInList();
}

function renderSelectedGroupInList() {
    const cards = groupsList.querySelectorAll('.group-card');

    cards.forEach(card => {
        const groupId = Number(card.dataset.groupId);
        card.classList.toggle('selected', selectedGroup && groupId === selectedGroup.id);
    });
}

function handleGroupListClick(event) {
    const card = event.target.closest('.group-card');

    if (!card) {
        return;
    }

    const groupId = Number(card.dataset.groupId);
    const group = loadedGroups.find(currentGroup => currentGroup.id === groupId);

    if (group) {
        selectGroup(group);
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

async function deleteMember(memberId) {
    if (!selectedGroup) {
        return;
    }

    try {
        const response = await fetch(`/api/groups/${selectedGroup.id}/members/${memberId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        showMemberMessage('');
        await loadMembers(selectedGroup.id);
    } catch (error) {
        showMemberMessage('Could not remove the member. Please try again.', true);
        console.error(error);
    }
}

function handleMembersListClick(event) {
    const deleteButton = event.target.closest('.delete-member-button');

    if (!deleteButton) {
        return;
    }

    deleteMember(Number(deleteButton.dataset.memberId));
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('createForm');
    const refreshButton = document.getElementById('refreshBtn');
    const submitButton = form.querySelector('button[type="submit"]');

    loadGroups();

    refreshButton.addEventListener('click', loadGroups);
    groupsList.addEventListener('click', handleGroupListClick);
    membersList.addEventListener('click', handleMembersListClick);

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

            await response.json();
            form.reset();
            showMessage('Group created successfully.');
            await loadGroups();
        } catch (error) {
            showMessage('Could not create the group. Please check the form and try again.', true);
            console.error(error);
        } finally {
            submitButton.disabled = false;
        }
    });

    memberForm.addEventListener('submit', async event => {
        event.preventDefault();
        showMemberMessage('');

        if (!selectedGroup) {
            showMemberMessage('Select a group before adding members.', true);
            return;
        }

        const name = memberNameInput.value.trim();

        if (!name) {
            showMemberMessage('Member name is required.', true);
            return;
        }

        addMemberButton.disabled = true;

        try {
            const response = await fetch(`/api/groups/${selectedGroup.id}/members`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({name})
            });

            if (!response.ok) {
                throw new Error(await response.text());
            }

            memberForm.reset();
            showTimedMemberMessage('Member added successfully.');
            await loadMembers(selectedGroup.id);
        } catch (error) {
            showMemberMessage('Could not add the member. Please check the form and try again.', true);
            console.error(error);
        } finally {
            addMemberButton.disabled = false;
        }
    });
});
