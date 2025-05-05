<template>
    <div class="table-container">
      <!-- Header -->
      <div class="table-header">
        <h2 class="table-title">Persons Table</h2>
        <div class="button-group" v-click-outside="() => (dropdownOpen = false)">
          <div class="download-wrapper">
            <button @click="dropdownOpen = !dropdownOpen" class="primary-btn">
              ⬇ Download
              <span v-if="isLoading" class="spinner"></span>
            </button>
            <ul v-if="dropdownOpen" class="dropdown-menu">
              <li @click="downloadExport('json')">Download JSON</li>
              <li @click="downloadExport('csv')">Download CSV</li>
              <li @click="downloadExport('pdf')">Download PDF</li>
            </ul>
          </div>
          <button @click="openDrawer()" class="primary-btn">➕ Add Person</button>
        </div>
      </div>

      <!-- Filters -->
      <div class="table-toolbar">
        <input v-model="searchQuery" type="text" placeholder="Search by name or email..." />

        <label>
          Filter by Country:
          <select v-model="countryFilter">
            <option value="">All</option>
            <option v-for="country in allCountries" :key="country.id" :value="country.name">
              {{ country.name }}
            </option>
          </select>
        </label>

        <label>
          Filter by Language:
          <select v-model="languageFilter">
            <option value="">All</option>
            <option v-for="lang in allLanguages" :key="lang.id" :value="lang.name">
              {{ lang.name }}
            </option>
          </select>
        </label>

        <label>
          Rows per page:
          <select v-model.number="perPage">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="25">25</option>
          </select>
        </label>
      </div>

      <!-- Table -->
      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th @click="sortBy('name')">Name</th>
              <th @click="sortBy('surname')">Surname</th>
              <th @click="sortBy('email')">Email</th>
              <th @click="sortBy('phone')">Phone</th>
              <th @click="sortBy('birthdate')">Birthdate</th>
              <th>Country</th>
              <th>Languages</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="paginatedPersons.length === 0">
              <td colspan="9" class="empty">📭 No persons found.</td>
            </tr>
            <tr v-for="person in paginatedPersons" :key="person.id">
              <td>{{ person.id }}</td>
              <td>{{ person.name }}</td>
              <td>{{ person.surname }}</td>
              <td>{{ person.email }}</td>
              <td>{{ person.phone }}</td>
              <td>{{ person.birthdate }}</td>
              <td>{{ person.country }}</td>
              <td>{{ person.languages.join(', ') }}</td>
              <td>
                <button class="action-btn edit" @click="openDrawer(person)">Edit</button>
                <button class="action-btn delete" @click="deletePerson(person.id)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="pagination">
        <button :disabled="currentPage === 1" @click="currentPage--">Prev</button>
        <span>Page {{ currentPage }} / {{ totalPages }}</span>
        <button :disabled="currentPage === totalPages" @click="currentPage++">Next</button>
      </div>

      <!-- Drawer -->
      <PersonDrawer
        :visible="drawerVisible"
        :personData="selectedPerson"
        @close="drawerVisible = false"
        @submit="handleSubmit"
      />
    </div>
  </template>

  <script>
  import { mapGetters, mapActions } from 'vuex'
  import PersonDrawer from './PersonDrawer.vue'
  import '../assets/styles/person-table.css'

  export default {
    components: { PersonDrawer },
    directives: {
      clickOutside: {
        beforeMount(el, binding) {
          el.clickOutsideEvent = event => {
            if (!(el === event.target || el.contains(event.target))) {
              binding.value(event)
            }
          }
          document.addEventListener('click', el.clickOutsideEvent)
        },
        unmounted(el) {
          document.removeEventListener('click', el.clickOutsideEvent)
        }
      }
    },
    data() {
      return {
        searchQuery: '',
        sortKey: '',
        sortAsc: true,
        currentPage: 1,
        perPage: 5,
        drawerVisible: false,
        selectedPerson: {},
        dropdownOpen: false,
        isLoading: false,
        countryFilter: '',
        languageFilter: ''
      }
    },
    computed: {
      ...mapGetters(['allPersons', 'allLanguages', 'allCountries']),
      filteredPersons() {
        const q = this.searchQuery.toLowerCase()
        return this.allPersons
          .filter(p =>
            p.name.toLowerCase().includes(q) || p.email.toLowerCase().includes(q)
          )
          .filter(p =>
            this.countryFilter ? p.country === this.countryFilter : true
          )
          .filter(p =>
            this.languageFilter ? p.languages.includes(this.languageFilter) : true
          )
      },
      sortedPersons() {
        if (!this.sortKey) return this.filteredPersons
        return [...this.filteredPersons].sort((a, b) => {
          let valA = a[this.sortKey]
          let valB = b[this.sortKey]
          if (typeof valA === 'string') valA = valA.toLowerCase()
          if (typeof valB === 'string') valB = valB.toLowerCase()
          return this.sortAsc ? valA.localeCompare(valB) : valB.localeCompare(valA)
        })
      },
      paginatedPersons() {
        const start = (this.currentPage - 1) * this.perPage
        return this.sortedPersons.slice(start, start + this.perPage)
      },
      totalPages() {
        return Math.ceil(this.sortedPersons.length / this.perPage)
      }
    },
    methods: {
      ...mapActions([
        'fetchPersons',
        'fetchLanguages',
        'fetchCountries',
        'addPerson',
        'updatePerson',
        'deletePerson',
        'exportPersons'
      ]),
      sortBy(key) {
        this.sortKey === key ? (this.sortAsc = !this.sortAsc) : (this.sortKey = key)
      },
      openDrawer(person = {}) {
        this.selectedPerson = { ...person }
        this.drawerVisible = true
      },
      async handleSubmit(formData) {
        if (formData.id) {
          await this.updatePerson(formData)
        } else {
          await this.addPerson(formData)
        }
        this.drawerVisible = false
        this.fetchPersons()
      },
      async downloadExport(format) {
        this.isLoading = true
        try {
          await this.exportPersons(format)
        } catch {
          alert('Download failed.')
        }
        this.isLoading = false
        this.dropdownOpen = false
      }
    },
    mounted() {
      this.fetchPersons()
      this.fetchLanguages()
      this.fetchCountries()
    },
    watch: {
      searchQuery() {
        this.currentPage = 1
      },
      perPage() {
        this.currentPage = 1
      },
      countryFilter() {
        this.currentPage = 1
      },
      languageFilter() {
        this.currentPage = 1
      }
    }
  }
  </script>


  <style scoped>

  </style>
