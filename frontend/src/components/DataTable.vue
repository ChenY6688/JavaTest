<template>
    <div v-if="data.length" class="table-container">
        <table>
            <thead>
                <tr>
                    <th class="resizable" v-for="(header, index) in headers" :key="index"
                        :style="{ width: columnWidths[index] + 'px' }" @mousedown="onResizeStart(index)">
                        {{ header }}
                        <div class="resize-handle"></div>
                    </th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(row, index) in data" :key="index">
                    <td>{{ row.benefit }}</td>
                    <td>{{ row.coverage }}</td>
                    <td>{{ row.category }}</td>
                    <td>{{ row.planName }}</td>
                    <td>{{ row.coverageValue }}</td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script setup>
import { ref } from "vue";
import { defineProps } from 'vue';

const props = defineProps({
    headers: {
        type: Array,
        default: null,
    },
    data: {
        type: Array,
        default: null,
    },
});

let isResizing = false;
let startX = 0;
let startWidth = 0;
let columnIndex = 0;

const columnWidths = ref([200, 200, 200, 200, 200]);

const onResizeStart = (index) => {
    isResizing = true;
    columnIndex = index;
    startX = event.clientX;
    startWidth = columnWidths.value[index];
    document.addEventListener("mousemove", onResize);
    document.addEventListener("mouseup", onResizeEnd);
};

const onResize = (event) => {
    if (isResizing) {
        const diff = event.clientX - startX;
        columnWidths.value[columnIndex] = startWidth + diff;
    }
};

const onResizeEnd = () => {
    isResizing = false;
    document.removeEventListener("mousemove", onResize);
    document.removeEventListener("mouseup", onResizeEnd);
};
</script>

<style scoped>
.table-container {
    margin-top: 20px;
    width: 100%;
    overflow-x: auto;
}

table {
    width: 100%;
    border-collapse: collapse;
}

th,
td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
}

th {
    background-color: #f4f4f4;
}

th.resizable {
    position: relative;
    cursor: ew-resize;
}

th .resize-handle {
    position: absolute;
    right: 0;
    top: 0;
    width: 5px;
    height: 100%;
    background-color: transparent;
}

th.resizable:hover .resize-handle {
    background-color: #888;
}
</style>