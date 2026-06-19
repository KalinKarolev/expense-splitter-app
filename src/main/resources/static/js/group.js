const groupTitle = document.getElementById('groupTitle');
const groupDescription = document.getElementById('groupDescription');
const memberForm = document.getElementById('memberForm');
const memberNameInput = document.getElementById('memberName');
const addMemberButton = document.getElementById('addMemberBtn');
const memberMessage = document.getElementById('memberMessage');
const membersEmpty = document.getElementById('membersEmpty');
const membersList = document.getElementById('membersList');
const expenseForm = document.getElementById('expenseForm');
const expenseDescriptionInput = document.getElementById('expenseDescription');
const expenseAmountInput = document.getElementById('expenseAmount');
const expensePayerSelect = document.getElementById('expensePayer');
const addExpenseButton = document.getElementById('addExpenseBtn');
const expenseMessage = document.getElementById('expenseMessage');
const expenseMembersHint = document.getElementById('expenseMembersHint');
const expensesEmpty = document.getElementById('expensesEmpty');
const expensesList = document.getElementById('expensesList');
const balancesSummary = document.getElementById('balancesSummary');
const balancesEmpty = document.getElementById('balancesEmpty');
const balancesList = document.getElementById('balancesList');
const settlementsSummary = document.getElementById('settlementsSummary');
const settlementsEmpty = document.getElementById('settlementsEmpty');
const settlementsList = document.getElementById('settlementsList');

let memberMessageTimeoutId = null;
let expenseMessageTimeoutId = null;
let groupMembers = [];
let groupExpenses = [];

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

function showExpenseMessage(message, isError = false) {
    clearExpenseMessageTimer();
    expenseMessage.textContent = message;
    expenseMessage.classList.toggle('error', isError);
}

function showTimedExpenseMessage(message) {
    showExpenseMessage(message);
    expenseMessageTimeoutId = window.setTimeout(() => {
        expenseMessage.textContent = '';
        expenseMessageTimeoutId = null;
    }, 3000);
}

function clearExpenseMessageTimer() {
    if (expenseMessageTimeoutId) {
        window.clearTimeout(expenseMessageTimeoutId);
        expenseMessageTimeoutId = null;
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
        groupMembers = members;
        renderMembers(members);
        renderPayerOptions(members);
    } catch (error) {
        showMemberMessage('Could not load members for this group.', true);
        console.error(error);
    }
}

async function loadExpenses(groupId) {
    try {
        const response = await fetch(`/api/groups/${groupId}/expenses`);

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        const expenses = await response.json();
        groupExpenses = expenses;
        renderExpenses(expenses);
    } catch (error) {
        showExpenseMessage('Could not load expenses for this group.', true);
        console.error(error);
    }
}

async function loadBalances(groupId) {
    try {
        const response = await fetch(`/api/groups/${groupId}/balances`);

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        const balances = await response.json();
        renderBalances(balances);
    } catch (error) {
        balancesSummary.textContent = 'Could not load balances for this group.';
        balancesEmpty.style.display = 'block';
        balancesEmpty.textContent = 'Could not load balances.';
        console.error(error);
    }
}

async function loadSettlements(groupId) {
    try {
        const response = await fetch(`/api/groups/${groupId}/settlements`);

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        const settlements = await response.json();
        renderSettlements(settlements);
    } catch (error) {
        settlementsSummary.textContent = 'Could not load settlement suggestions for this group.';
        settlementsEmpty.style.display = 'block';
        settlementsEmpty.textContent = 'Could not load settlement suggestions.';
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

function renderPayerOptions(members) {
    expensePayerSelect.innerHTML = '<option value="">Select payer</option>';

    members.forEach(member => {
        const option = document.createElement('option');
        option.value = member.id;
        option.textContent = member.name;
        expensePayerSelect.appendChild(option);
    });

    const hasMembers = members.length > 0;
    expenseDescriptionInput.disabled = !hasMembers;
    expenseAmountInput.disabled = !hasMembers;
    expensePayerSelect.disabled = !hasMembers;
    addExpenseButton.disabled = !hasMembers;
    expenseMembersHint.textContent = hasMembers
        ? 'Register expenses paid by members of this group.'
        : 'Add at least one member before registering expenses.';
}

function renderExpenses(expenses) {
    expensesList.innerHTML = '';

    if (!expenses || expenses.length === 0) {
        expensesEmpty.textContent = 'No expenses added to this group yet.';
        expensesEmpty.style.display = 'block';
        return;
    }

    expensesEmpty.style.display = 'none';

    expenses.forEach(expense => {
        const item = document.createElement('li');
        item.className = 'expense-card';

        const details = document.createElement('div');
        details.className = 'expense-card-details';

        const description = document.createElement('span');
        description.textContent = expense.description;

        const meta = document.createElement('small');
        meta.textContent = `Paid by ${expense.payerName}`;

        details.append(description, meta);

        const amount = document.createElement('strong');
        amount.textContent = formatAmount(expense.amount);

        item.append(details, amount);
        expensesList.appendChild(item);
    });
}

function renderBalances(balances) {
    balancesList.innerHTML = '';

    if (!balances || balances.length === 0) {
        balancesSummary.textContent = 'Add members before calculating balances.';
        balancesEmpty.textContent = 'No members added to this group yet.';
        balancesEmpty.style.display = 'block';
        return;
    }

    const totalExpenses = balances.reduce((total, balance) => total + Number(balance.paidAmount), 0);
    balancesSummary.textContent = `Total group expenses: ${formatAmount(totalExpenses)}`;

    if (totalExpenses === 0) {
        balancesEmpty.textContent = 'No expenses added yet, so all balances are zero.';
        balancesEmpty.style.display = 'block';
    } else {
        balancesEmpty.style.display = 'none';
    }

    balances.forEach(balance => {
        const item = document.createElement('li');
        item.className = 'balance-card';

        const details = document.createElement('div');
        details.className = 'balance-card-details';

        const name = document.createElement('span');
        name.textContent = balance.memberName;

        const meta = document.createElement('small');
        meta.textContent = `Paid ${formatAmount(balance.paidAmount)} | Share ${formatAmount(balance.equalShare)}`;

        details.append(name, meta);

        const amount = document.createElement('strong');
        const memberBalance = Number(balance.balance);
        amount.textContent = formatBalanceMeaning(balance.memberName, memberBalance);
        amount.className = getBalanceClass(memberBalance);

        item.append(details, amount);
        balancesList.appendChild(item);
    });
}

function renderSettlements(settlements) {
    settlementsList.innerHTML = '';

    if (!settlements || settlements.length === 0) {
        settlementsEmpty.textContent = groupExpenses.length === 0
            ? 'No expenses added yet, so there are no settlement suggestions.'
            : 'Everyone is settled.';
        settlementsEmpty.style.display = 'block';
        settlementsSummary.textContent = 'Suggested payments will appear when members owe each other money.';
        return;
    }

    settlementsEmpty.style.display = 'none';
    settlementsSummary.textContent = 'These payments settle the current group balances.';

    settlements.forEach(settlement => {
        const item = document.createElement('li');
        item.className = 'settlement-card';

        const details = document.createElement('div');
        details.className = 'settlement-card-details';

        const text = document.createElement('span');
        text.textContent = `${settlement.fromMemberName} pays ${settlement.toMemberName}`;

        const meta = document.createElement('small');
        meta.textContent = 'Suggested settlement';

        details.append(text, meta);

        const amount = document.createElement('strong');
        amount.textContent = formatAmount(settlement.amount);

        item.append(details, amount);
        settlementsList.appendChild(item);
    });
}

function formatAmount(amount) {
    return Number(amount).toFixed(2);
}

function formatBalanceMeaning(memberName, amount) {
    if (amount > 0) {
        return `${memberName} should receive ${formatAmount(amount)}`;
    }

    if (amount < 0) {
        return `${memberName} owes ${formatAmount(Math.abs(amount))}`;
    }

    return `${memberName} is settled`;
}

function getBalanceClass(amount) {
    if (amount > 0) {
        return 'balance-positive';
    }

    if (amount < 0) {
        return 'balance-negative';
    }

    return 'balance-zero';
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
        await loadBalances(groupId);
        await loadSettlements(groupId);
    } catch (error) {
        showMemberMessage('Could not add the member. Please check the form and try again.', true);
        console.error(error);
    } finally {
        addMemberButton.disabled = false;
    }
}

async function addExpense(groupId) {
    showExpenseMessage('');

    if (groupMembers.length === 0) {
        showExpenseMessage('Add a member before registering expenses.', true);
        return;
    }

    const description = expenseDescriptionInput.value.trim();
    const amount = expenseAmountInput.value;
    const payerId = Number(expensePayerSelect.value);

    if (!description) {
        showExpenseMessage('Expense description is required.', true);
        return;
    }

    if (!amount || Number(amount) <= 0) {
        showExpenseMessage('Expense amount must be positive.', true);
        return;
    }

    if (!payerId) {
        showExpenseMessage('Select a payer for this expense.', true);
        return;
    }

    addExpenseButton.disabled = true;

    try {
        const response = await fetch(`/api/groups/${groupId}/expenses`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({description, amount, payerId})
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        expenseForm.reset();
        showTimedExpenseMessage('Expense added successfully.');
        await loadExpenses(groupId);
        await loadBalances(groupId);
        await loadSettlements(groupId);
    } catch (error) {
        showExpenseMessage('Could not add the expense. Please check the form and try again.', true);
        console.error(error);
    } finally {
        addExpenseButton.disabled = groupMembers.length === 0;
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
        await loadExpenses(groupId);
        await loadBalances(groupId);
        await loadSettlements(groupId);
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
    await loadExpenses(groupId);
    await loadBalances(groupId);
    await loadSettlements(groupId);

    memberForm.addEventListener('submit', event => {
        event.preventDefault();
        addMember(groupId);
    });

    expenseForm.addEventListener('submit', event => {
        event.preventDefault();
        addExpense(groupId);
    });

    membersList.addEventListener('click', event => {
        const deleteButton = event.target.closest('.delete-member-button');

        if (deleteButton) {
            deleteMember(groupId, Number(deleteButton.dataset.memberId));
        }
    });
});
