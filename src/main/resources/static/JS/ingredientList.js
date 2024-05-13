function addIngredientToList() {
    const select = document.getElementById('ingredientSelect');
    const weight = document.getElementById('ingredientWeight').value;
    const list = document.getElementById('ingredientList');
    const selectedOption = select.options[select.selectedIndex];

    //Checks if the user has selected an ingredient and weight
    if (!selectedOption.value || weight.trim() === '' || weight.trim() === "0") {
        alert('Vælg en ingrediens eller indtast vægten!');
        return;
    }

    // Create list item for ingredient
    const li = document.createElement('li');
    li.textContent = `${selectedOption.text} - ${weight} gram`;

    // Append hidden input for ingredientID
    const inputId = document.createElement('input');
    inputId.type = 'hidden';
    inputId.name = 'ingredientIds';
    inputId.value = selectedOption.value;
    //Saves the hidden values in the list
    li.appendChild(inputId);

    // Append hidden input for weight
    const inputWeight = document.createElement('input');
    inputWeight.type = 'hidden';
    inputWeight.name = 'weights';
    inputWeight.value = weight;

    li.appendChild(inputWeight);
    list.appendChild(li);
}

