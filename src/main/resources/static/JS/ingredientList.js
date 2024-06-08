function addIngredientToList() {
    const select = document.getElementById('ingredientSelect');
    const weight = document.getElementById('ingredientWeight').value;
    const ingredientList = document.getElementById('ingredientList');
    const selectedOption = select.options[select.selectedIndex];

    // Checks if the user has selected an ingredient and weight
    if (!selectedOption.value || weight.trim() === '' || weight.trim() === "0") {
        alert('Vælg en ingrediens eller indtast vægten!');
        return;
    }

    // Create the list of added ingredients that is being shown to the user
    const listContent = document.createElement('li');
    listContent.textContent = `${selectedOption.text} - ${weight} gram`;

    // Append hidden input for ingredientID
    const inputId = document.createElement('input');
    inputId.type = 'hidden';
    inputId.name = 'ingredientIds';
    inputId.value = selectedOption.value;
    listContent.appendChild(inputId);

    // Append hidden input for weight
    const inputWeight = document.createElement('input');
    inputWeight.type = 'hidden';
    inputWeight.name = 'weights';
    inputWeight.value = weight;
    listContent.appendChild(inputWeight);

    // Create and append delete button
    const deleteButton = document.createElement('button');
    deleteButton.textContent = 'Fjern';
    deleteButton.type = 'button';
    deleteButton.onclick = function() {
        removeIngredientFromList(listContent);
    };
    listContent.appendChild(deleteButton);

    // Append the list item to the ingredient list
    ingredientList.appendChild(listContent);
}

function removeIngredientFromList(listItem) {
    const ingredientList = document.getElementById('ingredientList');
    ingredientList.removeChild(listItem);
}




