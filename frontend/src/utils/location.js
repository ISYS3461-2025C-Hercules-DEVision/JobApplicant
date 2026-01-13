// src/utils/location.js
import { Country, State } from "country-state-city";

/**
 * Return: [{ name, isoCode }]
 */
export function getCountries() {
  return Country.getAllCountries()
    .map((c) => ({
      name: c.name,
      isoCode: c.isoCode, // e.g. "VN", "AU"
    }))
    .sort((a, b) => a.name.localeCompare(b.name));
}

/**
 * countryIso: "VN"
 * Return: [{ name, isoCode }]
 */
export function getStatesByCountry(countryIso) {
  if (!countryIso) return [];
  return State.getStatesOfCountry(countryIso)
    .map((s) => ({
      name: s.name, // e.g. "Ho Chi Minh", "New South Wales"
      isoCode: s.isoCode, // e.g. "SG", "NSW" (state code)
    }))
    .sort((a, b) => a.name.localeCompare(b.name));
}
