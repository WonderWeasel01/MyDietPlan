document.addEventListener('DOMContentLoaded', function() {
    // Populate the ingredients select dropdown
    populateIngredientsDropdown();

    // Attach event listener to the form
    const form = document.getElementById('recipeForm');
    form.addEventListener('submit', submitRecipe);

    // Attach event listener to the add ingredient button
    document.getElementById('addIngredientBtn').addEventListener('click', addIngredientToList);
});

function populateIngredientsDropdown() {
    fetch('/admin')
        .then(response => response.json())
        .then(ingredients => {
            const select = document.getElementById('ingredientSelect');
            ingredients.forEach(ingredient => {
                const option = document.createElement('option');
                option.value = ingredient.id;
                option.textContent = ingredient.name;
                select.appendChild(option);
            });
        })
        .catch(error => console.error('Error loading ingredients:', error));
}

function addIngredientToList() {
    const select = document.getElementById('ingredientSelect');
    const selectedOption = select.options[select.selectedIndex];
    const list = document.getElementById('ingredientList');

    if (!selectedOption.value) return;

    if (Array.from(list.children).some(item => item.dataset.id === selectedOption.value)) {
        alert('This ingredient has already been added.');
        return;
    }

    const li = document.createElement('li');
    li.textContent = selectedOption.textContent;
    li.dataset.id = selectedOption.value;
    list.appendChild(li);
}

function submitRecipe(event) {
    event.preventDefault();
    const ingredientIds = Array.from(document.getElementById('ingredientList').children)
        .map(li => li.dataset.id);

    console.log('Submitting recipe with ingredient IDs:', ingredientIds);}
// You could here serialize the form
