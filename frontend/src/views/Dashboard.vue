<template>
    <div class="upload-container">
        <h2>Document Formatter</h2>

        <div class="upload-section">
            <input type="file" accept=".xlsx" @change="handleFileUpload" style="margin-left: 5rem;" />
        </div>

        <button :disabled="!file" @click="submitFile">提交</button>

        <DataTable :data="tableData" :headers="headers"/>
    </div>
</template>

<script setup>
import { ref } from "vue";
import DataTable from '@/components/DataTable.vue';
import useAxios from '@/hooks/useAxios';
import Swal from 'sweetalert2';

const { api } = useAxios();

const headers = ["Benefit", "Coverage", "Category", "Plan Name", "Coverage Value"];

const file = ref(null);
const tableData = ref([]);

const handleFileUpload = (event) => {
    const uploadedFile = event.target.files[0];
    if (uploadedFile && uploadedFile.type === "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") {
        file.value = uploadedFile;
    } else {
        file.value = null;
        event.target.value = "";
    }
};

const submitFile = async () => {
    if (!file.value) return;

    const formData = new FormData();
    formData.append("file", file.value);

    try {
        const { data } = await api.post('/api/document/upload', formData);
        tableData.value = data
        Swal.fire({
            title: "格式化成功!",
            icon: "success",
            confirmButtonColor: "#154EC1",
            timer: 2000
        });
    } catch (error) {
        let err = error.message;
        if (error.response) { err = error.response.data.errMessage };
        Swal.fire({
            title: err,
            icon: "warning",
            iconColor: "#e65656",
            color: "#e65656",
            showCancelButton: true,
            showConfirmButton: false,
            timer: 2000
        });
        tableData.value = [];
    }
    file.value = null;
};

</script>

<style scoped>
.upload-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    border: 1px solid #ccc;
    border-radius: 8px;
    max-width: 1200px;
    margin: 0 auto;
}

h2 {
    margin-bottom: 20px;
    text-align: center;
}

.upload-section {
    margin-bottom: 15px;
    text-align: center;
}

input[type="file"] {
    margin: 0 auto;
    display: block;
}

button {
    padding: 10px 15px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}
</style>