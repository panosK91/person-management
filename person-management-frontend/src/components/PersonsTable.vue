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

      <!-- Country Filter -->
      <div class="custom-dropdown" v-click-outside="() => (countryDropdownOpen = false)">
        <div class="dropdown-input-wrapper">
          <input
            type="text"
            v-model="countrySearch"
            placeholder="Search country..."
            @focus="countryDropdownOpen = true"
          />
          <button v-if="countrySearch" @click="countrySearch = ''" class="clear-btn">✕</button>
        </div>
        <ul v-if="countryDropdownOpen" class="dropdown-list">
          <li @click="selectCountry('')">All</li>
          <li
            v-for="country in filteredCountryOptions"
            :key="country.id"
            @click="selectCountry(country.name)"
          >
            {{ country.name }}
          </li>
        </ul>
      </div>

      <!-- Language Filter -->
      <div class="custom-dropdown" v-click-outside="() => (languageDropdownOpen = false)">
        <div class="dropdown-input-wrapper">
          <input
            type="text"
            v-model="languageSearch"
            placeholder="Search language..."
            @focus="languageDropdownOpen = true"
          />
          <button v-if="languageSearch" @click="languageSearch = ''" class="clear-btn">✕</button>
        </div>
        <ul v-if="languageDropdownOpen" class="dropdown-list">
          <li @click="selectLanguage('')">All</li>
          <li
            v-for="lang in filteredLanguageOptions"
            :key="lang.id"
            @click="selectLanguage(lang.name)"
          >
            {{ lang.name }}
          </li>
        </ul>
      </div>

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
      countrySearch: '',
      countryDropdownOpen: false,
      languageFilter: '',
      languageSearch: '',
      languageDropdownOpen: false
    }
  },
  computed: {
    ...mapGetters(['allPersons', 'allLanguages', 'allCountries']),
    filteredCountryOptions() {
      const q = this.countrySearch.toLowerCase()
      return this.allCountries.filter(c => c.name.toLowerCase().includes(q))
    },
    filteredLanguageOptions() {
      const q = this.languageSearch.toLowerCase()
      return this.allLanguages.filter(l => l.name.toLowerCase().includes(q))
    },
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
    },
    selectCountry(name) {
      this.countryFilter = name
      this.countrySearch = name
      this.countryDropdownOpen = false
    },
    selectLanguage(name) {
      this.languageFilter = name
      this.languageSearch = name
      this.languageDropdownOpen = false
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
    },
    countrySearch(value) {
      if (value.trim() === '') {
        this.selectCountry('')
      }
    },
    languageSearch(value) {
      if (value.trim() === '') {
        this.selectLanguage('')
      }
    }
  }
}
</script>

<style scoped>

</style>
