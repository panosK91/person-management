<template>
    <div v-if="visible" class="drawer">
      <div class="drawer-header">
        <h3>{{ isEdit ? 'Edit' : 'Add' }} Person</h3>
        <button class="close-btn" @click="closeDrawer">✖</button>
      </div>

      <div class="drawer-content">
        <form class="drawer-form" @submit.prevent="submitForm">
          <label>Name</label>
          <input v-model="person.name" type="text" />
          <p v-if="validationErrors.name" class="error">{{ validationErrors.name }}</p>

          <label>Surname</label>
          <input v-model="person.surname" type="text" />
          <p v-if="validationErrors.surname" class="error">{{ validationErrors.surname }}</p>

          <label>Email</label>
          <input v-model="person.email" type="email" />

          <label>Phone</label>
          <input v-model="person.phone" type="text" />

          <label>Birthdate</label>
          <input v-model="person.birthdate" type="date" />
          <p v-if="validationErrors.birthdate" class="error">{{ validationErrors.birthdate }}</p>

          <label>Street</label>
          <input v-model="person.address.street" type="text" />

          <label>City</label>
          <input v-model="person.address.city" type="text" />

          <label>State</label>
          <input v-model="person.address.state" type="text" />

          <label>Zipcode</label>
          <input v-model="person.address.zipcode" type="text" />

          <label>Country</label>
          <div class="dropdown country-dropdown" ref="countryDropdown">
            <div class="dropdown-display" @click="toggleCountryDropdown">
              {{ selectedCountryName || 'Select a country' }}
            </div>
            <div v-if="countryDropdownOpen" class="dropdown-list">
              <input
                v-model="countrySearchQuery"
                type="text"
                placeholder="Search country..."
                class="dropdown-search"
              />
              <div
                v-for="country in filteredCountries"
                :key="country.id"
                class="dropdown-item"
                @click.stop="selectCountry(country.id)"
              >
                {{ country.name }}
              </div>
            </div>
          </div>
          <p v-if="validationErrors.countryId" class="error">{{ validationErrors.countryId }}</p>

          <label>Languages</label>
          <div ref="dropdown" class="dropdown language-dropdown">
            <div class="dropdown-display" @click="toggleDropdown">
              {{ selectedLanguagesText || 'Select languages...' }}
            </div>
            <div v-if="dropdownOpen" class="dropdown-list">
              <input
                v-model="searchQuery"
                type="text"
                placeholder="Search languages..."
                class="dropdown-search"
              />
              <div
                v-for="lang in filteredLanguages"
                :key="lang.id"
                class="dropdown-item"
                :class="{ selected: isSelected(lang.name) }"
                @click.stop="toggleLanguage(lang.name)"
              >
                <input type="checkbox" :checked="isSelected(lang.name)" readonly />
                <span>{{ lang.name }}</span>
              </div>
            </div>
          </div>
          <p v-if="validationErrors.languages" class="error">{{ validationErrors.languages }}</p>
        </form>
      </div>

      <div class="form-buttons">
        <button type="submit" @click="submitForm">{{ isEdit ? 'Update' : 'Create' }}</button>
        <button type="button" class="cancel-btn" @click="closeDrawer">Cancel</button>
      </div>
    </div>
  </template>

  <script>
  import axios from 'axios'
  import { mapGetters } from 'vuex'
  import '../assets/styles/person-drawer.css'

  export default {
    props: {
      visible: Boolean,
      personData: Object
    },
    emits: ['close', 'submit'],
    data() {
      return {
        person: {
          id: null,
          name: '',
          surname: '',
          email: '',
          phone: '',
          birthdate: '',
          addressId: null,
          address: {
            street: '',
            city: '',
            state: '',
            zipcode: ''
          },
          countryId: '',
          languages: []
        },
        searchQuery: '',
        dropdownOpen: false,
        countryDropdownOpen: false,
        countrySearchQuery: '',
        validationErrors: {}
      }
    },
    computed: {
      ...mapGetters(['allLanguages', 'allCountries']),
      isEdit() {
        return !!this.person.id
      },
      selectedLanguagesText() {
        return this.person.languages.join(', ')
      },
      selectedCountryName() {
        const country = this.allCountries.find(c => c.id === this.person.countryId)
        return country ? country.name : ''
      },
      filteredLanguages() {
        const q = this.searchQuery.toLowerCase()
        return this.allLanguages.filter(lang =>
          lang.name.toLowerCase().includes(q)
        )
      },
      filteredCountries() {
        return this.allCountries.filter(c =>
          c.name.toLowerCase().includes(this.countrySearchQuery.toLowerCase())
        )
      }
    },
    watch: {
      personData: {
        immediate: true,
        async handler(newVal) {
          this.person = {
            id: newVal.id || null,
            name: newVal.name || '',
            surname: newVal.surname || '',
            email: newVal.email || '',
            phone: newVal.phone || '',
            birthdate: newVal.birthdate || '',
            addressId: newVal.addressId || null,
            address: {
              street: '',
              city: '',
              state: '',
              zipcode: ''
            },
            countryId: newVal.countryId || '',
            languages: newVal.languages ? [...newVal.languages] : []
          }

          if (newVal.addressId) {
            try {
              const res = await axios.get(`http://localhost:8080/api/addresses/${newVal.addressId}`)
              this.person.address = {
                street: res.data.street || '',
                city: res.data.city || '',
                state: res.data.state || '',
                zipcode: res.data.zipcode || ''
              }
            } catch (err) {
              console.error('Failed to fetch address:', err)
            }
          }
        }
      }
    },
    methods: {
      toggleDropdown() {
        this.dropdownOpen = !this.dropdownOpen
        if (this.dropdownOpen) this.searchQuery = ''
      },
      toggleCountryDropdown() {
        this.countryDropdownOpen = !this.countryDropdownOpen
        if (this.countryDropdownOpen) this.countrySearchQuery = ''
      },
      selectCountry(id) {
        this.person.countryId = id
        this.countryDropdownOpen = false
      },
      toggleLanguage(name) {
        const index = this.person.languages.indexOf(name)
        if (index === -1) this.person.languages.push(name)
        else this.person.languages.splice(index, 1)
      },
      isSelected(name) {
        return this.person.languages.includes(name)
      },
      submitForm() {
        const errors = {}

        if (!this.person.name.trim()) errors.name = 'Name is required'
        if (!this.person.surname.trim()) errors.surname = 'Surname is required'
        if (!this.person.birthdate) {
          errors.birthdate = 'Birthdate is required'
        } else {
          const selectedDate = new Date(this.person.birthdate)
          const today = new Date()
          today.setHours(0, 0, 0, 0)
          if (selectedDate >= today) {
            errors.birthdate = 'Birthdate must be before today'
          }
        }
        if (!this.person.countryId) {
          errors.countryId = 'Country is required'
        }
        if (this.person.languages.length === 0) {
          errors.languages = 'At least one language must be selected'
        }

        this.validationErrors = errors
        if (Object.keys(errors).length > 0) return

        this.$emit('submit', { ...this.person })
        this.dropdownOpen = false
        this.countryDropdownOpen = false
      },
      closeDrawer() {
        this.$emit('close')
        this.dropdownOpen = false
        this.countryDropdownOpen = false
      },
      handleClickOutside(e) {
        if (!this.$refs.dropdown?.contains(e.target)) {
          this.dropdownOpen = false
        }
        if (!this.$refs.countryDropdown?.contains(e.target)) {
          this.countryDropdownOpen = false
        }
      }
    },
    mounted() {
      document.addEventListener('click', this.handleClickOutside)
    },
    unmounted() {
      document.removeEventListener('click', this.handleClickOutside)
    }
  }
  </script>

  <style scoped>

  </style>
