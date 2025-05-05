<template>
  <div id="app">
    <PersonsTable />
  </div>
</template>

<script>
import PersonsTable from './components/PersonsTable.vue'

export default {
  name: 'App',
  components: {
    PersonsTable
  }
}
</script>
