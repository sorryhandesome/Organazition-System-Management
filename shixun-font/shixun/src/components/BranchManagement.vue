<template>
  <el-container>
    <el-header height="200px">
      <h2>部门管理</h2>
      <div class="toolbar">
        <el-form :inline="true" class="demo-form-inline" style="margin-top: 15px;">
          <el-form-item label="部门名称">
            <el-input v-model="searchName" placeholder="搜索部门名称" class="demo-form-inline" style="width: 300px;">
            </el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchState" placeholder="选择状态" clearable class="demo-form-inline" style="width: 200px">
              <el-option
                v-for="option in options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="onSearch">搜索</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Refresh" @click="refresh">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-button type="primary" :icon="Plus" @click="showAddDialog(null)">添加顶级部门</el-button>
      <el-button type="info" :icon="View" plain @click="toggleExpandAll">{{ isExpanded ? '折叠' : '展开' }}</el-button>
    </el-header>
    <el-main>
      <el-table
        :data="filteredData"
        style="width: 100%"
        :row-key="getRowKey"
        lazy
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        ref="tableRef"
        @expand-change="onExpandChange"
      >
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="num" label="编号" />
        <el-table-column prop="state" label="状态">
      <template #default="{ row }">
        <span v-if="row.state === true">
          <el-tag size="small">正常</el-tag>
        </span>
        <span v-else>
          <el-tag size="small" type="danger">禁用</el-tag>
        </span>
      </template>
    </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column fixed="right" label="操作" width="220">
          <template #default="scope">
            <el-button @click="editBranch(scope.row)" :icon="Edit" size="small">编辑</el-button>
            <el-button v-if="scope.row.branchId" @click="deleteBranch(scope.row)" :icon="Delete" size="small">删除</el-button>
            <el-button @click="showAddDialog(scope.row)" :icon="Plus" size="small">添加子部门</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog v-model="addDialogVisible" :title="dialogTitle">
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item v-if="isTopLevel" label="顶级公司" prop="tenementId">
            <el-select v-model="form.tenementId" placeholder="选择顶级公司">
              <el-option
                v-for="item in parentOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id||item.branchId"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-if="!isTopLevel" label="上级部门" prop="parentId">
            <el-input v-model="parentName"></el-input>
          </el-form-item>
          <el-form-item label="部门名称" prop="name" required>
            <el-input v-model="form.name"></el-input>
          </el-form-item>
          <el-form-item label="显示顺序" prop="num" required>
            <el-input v-model="form.num" type="number"></el-input>
          </el-form-item>
          <el-form-item label="负责人" prop="linkName">
            <el-input v-model="form.linkName"></el-input>
          </el-form-item>
          <el-form-item v-if="isTopLevel" label="联系电话" prop="phoneNum">
            <el-input v-model="form.phoneNum" type="number"></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email"></el-input>
          </el-form-item>
          <el-form-item label="部门状态" prop="state">
            <el-radio-group v-model="form.state">
              <el-radio :label="true">正常</el-radio>
              <el-radio :label="false">停用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="addDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="onSubmitForm">确定</el-button>
          </div>
        </template>
      </el-dialog>
    </el-main>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, nextTick, reactive } from 'vue';
import request from '@/utils/request';
import type { ElForm, ElTable } from 'element-plus';
import { cloneDeep } from 'lodash';
import {
  Check,
  Delete,
  Edit,
  Message,
  Search,
  Star,
  Plus,
  Refresh,
  View
} from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';

export default defineComponent({
  name: 'BranchManagement',
  setup() {
    const searchName = ref('');
    const searchState = ref<boolean | null>(null);
    const tableData = ref<any[]>([]);
    const addDialogVisible = ref(false);
    const formRef = ref<InstanceType<typeof ElForm> | null>(null);
    const isTopLevel = ref(false);
    const dialogTitle = ref('添加/编辑部门');
    const expandedRows = ref<Set<any>>(new Set());
    const tableRef = ref<InstanceType<typeof ElTable> | null>(null);
    const isExpanded = ref(false);
    const parentName = ref('');
    const identity = ref(window.sessionStorage.getItem('identity'));
    const storedData = JSON.parse(window.sessionStorage.getItem('user') || '{}');

    const form = reactive({
      id: null as number | null, // 添加 id 属性
      parentId: null as number | null,
      name: '',
      num: '',
      linkName: '',
      phoneNum: null as number | null,
      email: '',
      state: true,
      tenementId: null as number | null,
      branchId: null as number | null,
      createTime: null as string | null // 添加 createTime 字段
    });

    const parentOptions = ref<any[]>([]);
    const options = [
      { value: true, label: '正常' },
      { value: false, label: '停用' },
    ];
    const rules = ref({
      parentId: [{ required: true, message: '请选择上级部门', trigger: 'change' }],
      name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
      num: [{ required: true, message: '请输入显示顺序', trigger: 'blur' }],
      email: [{ type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }]
    });

    const getRowKey = (row: any) => row.branchId || row.id;

    const filterNodes = (nodes: any[], searchName: string, searchState: boolean | null) => {
      return nodes.reduce((acc: any[], node) => {
        const matchesName = node.name.includes(searchName);
        const matchesState = searchState === null || node.state === searchState;

        if (matchesName && matchesState) {
          acc.push(node);
          expandParents(node);
          return acc;
        }

        if (node.children) {
          const filteredChildren = filterNodes(node.children, searchName, searchState);
          if (filteredChildren.length) {
            acc.push({ ...node, children: filteredChildren });
            expandParents(node);
          }
        }

        return acc;
      }, []);
    };

    const expandParents = (node: any) => {
      let parent = node.parent;
      while (parent) {
        expandedRows.value.add(parent);
        parent = parent.parent;
      }
    };

    const onExpandChange = (row: any, expanded: boolean) => {
      if (expanded) {
        expandedRows.value.add(row);
      } else {
        expandedRows.value.delete(row);
      }
    };

    const filteredData = ref<any[]>([]);

    const onSearch = async () => {
      try {
        let data = [];
        if (identity.value === 'admin') {
          const response = await request.get('/branch/allTenementsWithBranches');
          data = response.data;
        } else if (identity.value === 'user') {
          const response = await request.get(`/branch/tenantsBranches?tenementId=${storedData.tenement.id}`);
          data = response.data;
        }

        const filtered = filterNodes(data, searchName.value, searchState.value);
        tableData.value = data;
        filteredData.value = filtered;
        await nextTick();
        expandedRows.value.forEach(row => {
          tableRef.value?.toggleRowExpansion(row, true);
        });
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    const deleteBranch = async (branch: any) => {
      ElMessageBox.confirm(
        '这将永久地删除他们，确认继续吗?',
        '警告',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
      await request.delete(`/branch/${branch.branchId || branch.id}`);
        onSearch(); // 删除后重新加载数据
        ElMessage({
            type: 'success',
            message: '删除成功',
        })
    }).catch(() => {
        ElMessage({
            type: 'info',
            message: '取消删除',
        })
    })
    };

    const editBranch = async (branch: any) => {
    try {
        dialogTitle.value = '编辑部门';
        parentName.value=branch.name;
        // 判断是否为顶级公司，可以通过检查是否有 tenementId 以及是否没有 parentId
        isTopLevel.value = !branch.parentId && !branch.tenementId;

        console.log('branch:', branch);
        console.log('isTopLevel:', isTopLevel.value);

        let response;
        if (isTopLevel.value) {
            // 如果是顶层公司，确保 id 存在且有效
            if (!branch.id) {
                console.error('Tenement ID is missing');
                ElMessage.error('无法获取顶层公司数据，缺少 Tenement ID');
                return;
            }
            response = await request.get(`/branch/tenement/${branch.id}`);
        } else {
            // 如果是下属部门，确保 branchId 存在且有效
            if (!branch.branchId) {
                console.error('Branch ID is missing');
                ElMessage.error('无法获取部门数据，缺少 Branch ID');
                return;
            }
            response = await request.get(`/branch/${branch.branchId}`);
        }

        // 确保响应数据存在并赋值给表单
        if (response && response.data) {
            Object.assign(form, response.data);
        } else {
            console.error('No data found in response');
        }

        await nextTick();
        addDialogVisible.value = true;
    } catch (error) {
        console.error('Error fetching branch data:', error);
    }
};

const showAddDialog = async (parent: any = null) => {
    try {
        isTopLevel.value = !parent;  // 顶级公司没有 parent 参数
        dialogTitle.value = isTopLevel.value ? '添加顶级部门' : '添加子部门';

        let response;
        if (identity.value === 'admin') {
            response = await request.get('/branch/allTenementsWithBranches');
        } else if (identity.value === 'user') {
            response = await request.get(`/branch/tenantsBranches?tenementId=${storedData.tenement.id}`);
        }

        if (response) {
            parentOptions.value = cloneDeep(response.data); // 获取完整的层次结构
        }

        if (parent) {
            // 子部门
            form.parentId = parent.branchId;
            form.tenementId = parent.tenementId || parent.id;
            parentName.value = parent.name; // 设置上级部门名称
        } else {
            // 顶级部门
            form.parentId = null;
            form.tenementId = null;
            parentName.value = ''; // 顶级部门没有上级
        }

        form.branchId = null;
        form.name = '';
        form.num = '';
        form.linkName = '';
        form.createTime = formatDate(new Date());
        form.phoneNum = null;
        form.email = '';
        form.state = true;

        await nextTick();
        addDialogVisible.value = true;
    } catch (error) {
        console.error('Error fetching tenements:', error);
    }
};

const onSubmitForm = async () => {
    try {
        formRef.value?.validate(async (valid: boolean) => {
            if (valid) {
                if (Array.isArray(form.tenementId)) {
                    form.tenementId = form.tenementId[form.tenementId.length - 1];
                }

                if (Array.isArray(form.parentId)) {
                    form.parentId = form.parentId[form.parentId.length - 1];
                }

                console.log("提交的数据：", JSON.stringify(form, null, 2)); // 输出调试信息
                if (isTopLevel.value && form.id) {
                    // 更新顶级公司的信息
                    await request.put(`/branch/tenement/${form.id}`, form);
                } else if (form.branchId) {
                    // 更新下属部门的信息
                    await request.put(`/branch/${form.branchId}`, form);
                } else {
                    // 添加新部门或顶级公司
                    await request.post('/branch/add', form);
                }
                addDialogVisible.value = false;
                onSearch();
                ElMessage({
                    type: 'success',
                    message: '操作成功',
                });
            }
        });
    } catch (error) {
        console.error('Error submitting form:', error);
        ElMessage.error('提交表单时出错');
    }
};

    const refresh = async () => {
      searchName.value = '';
      searchState.value = null;
      onSearch();
    };

    const toggleExpandAll = async () => {
      isExpanded.value = !isExpanded.value;
      tableData.value.forEach(row => {
        tableRef.value?.toggleRowExpansion(row, isExpanded.value);
        if (row.children) {
          row.children.forEach((child: any) => {
            tableRef.value?.toggleRowExpansion(child, isExpanded.value);
          });
        }
      });
    };

    const formatDate = (date:Date) => {
      if (!date) return '';
      const d = new Date(date);
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0'); // 月份补零
      const day = String(d.getDate()).padStart(2, '0'); // 日补零
      const hours = String(d.getHours()).padStart(2, '0'); // 小时补零
      const minutes = String(d.getMinutes()).padStart(2, '0'); // 分钟补零
      const seconds = String(d.getSeconds()).padStart(2, '0'); // 秒补零
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    };

    onMounted(() => {
      onSearch();
    });

    return {
      searchName,
      searchState,
      tableData,
      addDialogVisible,
      form,
      formRef,
      parentOptions,
      options,
      rules,
      getRowKey,
      filteredData,
      onSearch,
      deleteBranch,
      editBranch,
      showAddDialog,
      onSubmitForm,
      isTopLevel,
      dialogTitle,
      ElMessage,
       ElMessageBox,
      Check,
      Delete,
      Edit,
      Message,
      Search,
      Star,
      Plus,
      Refresh,
      View,
      refresh,
      onExpandChange,
      tableRef,
      expandedRows,
      isExpanded,
      toggleExpandAll,
      formatDate,
      parentName
    };
  },
});
</script>

<style scoped>
.input-with-select .el-input-group__append {
  background-color: #409EFF;
  color: white;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}
</style>
