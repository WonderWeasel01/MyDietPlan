// DOMContentLoaded = Only when the Document Object model (adminPage.html) is FULLY loaded will this code be executed
document.addEventListener('DOMContentLoaded', function() {
    // Populate the ingredients select dropdown
    populateIngredientsDropdown();

    // Attach event listener to the form
    const form = document.getElementById('recipeForm');
    form.addEventListener('submit', submitRecipe);

});


function populateIngredientsDropdown() {
    //Fetches the ingredientList and the recipe that is added to the model in the @GetMapping for /admin.
    fetch('/admin')
        //Converts the fetched data into a Javascript object "ingredients"
        .then(response => response.json())
        //Uses ingredients to populate the dropdown list. An option is created for each Ingredient in "ingredients".
        .then(ingredients => {
            const select = document.getElementById('ingredientSelect');
            ingredients.forEach(ingredient => {
                const option = document.createElement('option');
                option.value = ingredient.id;
                option.textContent = ingredient.name;
                select.appendChild(option);
            });
        })
        //If something goes wrong in the process, it gets written in console
        .catch(error => console.error('Error loading ingredients:', error));
}

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

/*
//The method that was added as event listener to the submit button.
function submitRecipe(event) {
    //Stops the html submit so this JavaScript document can take over the submission
    event.preventDefault();
    //Gets the ids from the ingredients in ingredientList
    const ingredientIds = Array.from(document.getElementById('ingredientList').children)
        .map(li => li.dataset.id);

    console.log('Submitting recipe with ingredient IDs:', ingredientIds);}


 */
