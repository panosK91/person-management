import { createStore } from 'vuex'
import axios from 'axios'

export default createStore({
  state: {
    persons: [],
    languages: [],
    countries: []
  },

  mutations: {
    setPersons(state, persons) {
      state.persons = persons
    },
    addPerson(state, person) {
      state.persons.push(person)
    },
    updatePerson(state, updated) {
      const index = state.persons.findIndex(p => p.id === updated.id)
      if (index !== -1) {
        state.persons.splice(index, 1, updated)
      }
    },
    deletePerson(state, id) {
      state.persons = state.persons.filter(p => p.id !== id)
    },
    setLanguages(state, languages) {
      state.languages = languages
    },
    setCountries(state, countries) {
      state.countries = countries
    }
  },

  actions: {
    async fetchPersons({ commit }) {
      const res = await axios.get('http://localhost:8080/api/persons')
      commit('setPersons', res.data)
    },

    async fetchLanguages({ commit }) {
      const res = await axios.get('http://localhost:8080/api/languages')
      commit('setLanguages', res.data)
    },

    async fetchCountries({ commit }) {
      try {
        const res = await axios.get('http://localhost:8080/api/countries')
        commit('setCountries', res.data)
      } catch (err) {
        console.error('Error fetching countries:', err)
      }
    },

    async addPerson({ commit, state }, formData) {
      const addressRes = await axios.post('http://localhost:8080/api/addresses', {
        street: formData.address.street,
        city: formData.address.city,
        state: formData.address.state,
        zipcode: formData.address.zipcode
      })
      const addressId = addressRes.data.id

      const languageIds = state.languages
        .filter(lang => formData.languages.includes(lang.name))
        .map(lang => lang.id)

      const personPayload = {
        name: formData.name,
        surname: formData.surname,
        birthdate: formData.birthdate,
        email: formData.email,
        phone: formData.phone,
        addressId,
        languageIds,
        countryId: formData.countryId
      }

      const res = await axios.post('http://localhost:8080/api/persons', personPayload)
      commit('addPerson', res.data)
    },

    async updatePerson({ commit, state }, formData) {
      await axios.put(`http://localhost:8080/api/addresses/${formData.addressId}`, {
        street: formData.address.street,
        city: formData.address.city,
        state: formData.address.state,
        zipcode: formData.address.zipcode
      })

      const languageIds = state.languages
        .filter(lang => formData.languages.includes(lang.name))
        .map(lang => lang.id)

      const personPayload = {
        name: formData.name,
        surname: formData.surname,
        birthdate: formData.birthdate,
        email: formData.email,
        phone: formData.phone,
        addressId: formData.addressId,
        languageIds,
        countryId: formData.countryId
      }

      const res = await axios.put(
        `http://localhost:8080/api/persons/${formData.id}`,
        personPayload
      )

      commit('updatePerson', res.data)
    },

    async deletePerson({ commit }, id) {
      await axios.delete(`http://localhost:8080/api/persons/${id}`)
      commit('deletePerson', id)
    },

    async exportPersons(_, format) {
      const url = `http://localhost:8080/api/export/${format}`

      try {
        const response = await axios.get(url, {
          responseType: 'blob'
        })
        const blob = new Blob([response.data])
        const downloadUrl = URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = downloadUrl
        link.download = `persons.${format}`
        document.body.appendChild(link)
        link.click()
        link.remove()
        URL.revokeObjectURL(downloadUrl)
      } catch (err) {
        console.error('Failed to download:', err)
        throw err
      }
    }
  },

  getters: {
    allPersons: state => state.persons,
    allLanguages: state => state.languages,
    allCountries: state => state.countries
  }
})
