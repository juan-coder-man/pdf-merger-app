const fileInput = document.getElementById("fileInput");
const fileList = document.getElementById("fileList");
const mergeButton = document.getElementById("mergeButton");
const statusMessage = document.getElementById("statusMessage");

// Estado local con los archivos seleccionados en el orden actual.
let selectedFiles = [];

fileInput.addEventListener("change", (event) => {
    const newFiles = Array.from(event.target.files || []);
    if (newFiles.length === 0) {
        return;
    }

    selectedFiles = [...selectedFiles, ...newFiles];
    fileInput.value = "";
    renderFiles();
});

mergeButton.addEventListener("click", async () => {
    if (selectedFiles.length === 0) {
        return;
    }

    setStatus("Procesando archivos...");
    mergeButton.disabled = true;

    // El backend recibe archivos y orden como arrays multipart.
    const formData = new FormData();
    selectedFiles.forEach((file) => formData.append("files", file));
    selectedFiles.forEach((_, index) => formData.append("order", String(index)));

    try {
        const response = await fetch("/api/pdf/merge", {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            const errorBody = await response.json().catch(() => null);
            const message = errorBody?.message || "No se pudo unificar los PDFs";
            throw new Error(message);
        }

        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = "merged.pdf";
        document.body.appendChild(link);
        link.click();
        link.remove();
        URL.revokeObjectURL(url);

        setStatus("PDF generado correctamente.");
    } catch (error) {
        setStatus(error.message || "Error inesperado.");
    } finally {
        // Rehabilita accion solo cuando existen archivos en memoria.
        mergeButton.disabled = selectedFiles.length === 0;
    }
});

// Refresca la lista visual respetando el orden actual del estado local.
function renderFiles() {
    fileList.innerHTML = "";

    selectedFiles.forEach((file, index) => {
        const item = document.createElement("li");
        item.className = "file-item";

        const name = document.createElement("span");
        name.className = "file-name";
        name.textContent = `${index + 1}. ${file.name}`;

        const actions = document.createElement("div");
        actions.className = "order-actions";

        const upButton = document.createElement("button");
        upButton.type = "button";
        upButton.textContent = "Subir";
        upButton.disabled = index === 0;
        upButton.addEventListener("click", () => moveFile(index, index - 1));

        const downButton = document.createElement("button");
        downButton.type = "button";
        downButton.textContent = "Bajar";
        downButton.disabled = index === selectedFiles.length - 1;
        downButton.addEventListener("click", () => moveFile(index, index + 1));

        const removeButton = document.createElement("button");
        removeButton.type = "button";
        removeButton.className = "remove-button";
        removeButton.textContent = "Quitar";
        removeButton.addEventListener("click", () => removeFile(index));

        actions.append(upButton, downButton, removeButton);
        item.append(name, actions);
        fileList.appendChild(item);
    });

    mergeButton.disabled = selectedFiles.length === 0;
    setStatus("");
}

// Reubica un archivo dentro del arreglo para reflejar el nuevo orden.
function moveFile(fromIndex, toIndex) {
    if (toIndex < 0 || toIndex >= selectedFiles.length) {
        return;
    }

    const updated = [...selectedFiles];
    const [moved] = updated.splice(fromIndex, 1);
    updated.splice(toIndex, 0, moved);
    selectedFiles = updated;
    renderFiles();
}

// Elimina un archivo del arreglo local segun su posicion actual.
function removeFile(index) {
    if (index < 0 || index >= selectedFiles.length) {
        return;
    }

    const updated = [...selectedFiles];
    updated.splice(index, 1);
    selectedFiles = updated;
    renderFiles();
}

// Actualiza el mensaje de estado expuesto al usuario.
function setStatus(message) {
    statusMessage.textContent = message;
}
