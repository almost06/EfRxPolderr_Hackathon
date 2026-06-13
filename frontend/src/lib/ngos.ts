export type NGO = {
  id: string;
  name: string;
  country: string;
  founded: number;
  budget: number; // annual EUR
  donors: number;
  tags: string[];
  description: string;
};

export const NGOS: NGO[] = [
  { id: "n1", name: "Clean Water Collective", country: "Kenya", founded: 2019, budget: 14000, donors: 82, tags: ["water", "sanitation", "africa", "health"], description: "Builds village wells and trains local maintenance teams." },
  { id: "n2", name: "Women Rise Foundation", country: "India", founded: 2015, budget: 18500, donors: 140, tags: ["women", "education", "rights", "asia"], description: "Vocational training and microloans for rural women." },
  { id: "n3", name: "Green Roots Reforestation", country: "Brazil", founded: 2017, budget: 19200, donors: 210, tags: ["climate", "forest", "environment", "biodiversity"], description: "Replants native species in deforested areas of the Amazon." },
  { id: "n4", name: "Ocean Plastic Watch", country: "Philippines", founded: 2020, budget: 9800, donors: 51, tags: ["climate", "ocean", "plastic", "environment"], description: "Coastal cleanups and plastic recycling cooperatives." },
  { id: "n5", name: "Books for Refugee Kids", country: "Lebanon", founded: 2018, budget: 12500, donors: 64, tags: ["education", "refugees", "children", "books"], description: "Mobile libraries for displaced children." },
  { id: "n6", name: "Solar Villages Initiative", country: "Tanzania", founded: 2016, budget: 16700, donors: 95, tags: ["climate", "energy", "solar", "africa"], description: "Off-grid solar kits for rural communities." },
  { id: "n7", name: "Sahel Food Resilience", country: "Mali", founded: 2014, budget: 19900, donors: 120, tags: ["food", "agriculture", "climate", "africa"], description: "Drought-resistant farming techniques and seed banks." },
  { id: "n8", name: "Andean Mothers Health", country: "Peru", founded: 2013, budget: 17500, donors: 110, tags: ["women", "health", "maternal", "latam"], description: "Maternal health clinics in high-altitude villages." },
  { id: "n9", name: "Global Water Trust", country: "United Kingdom", founded: 1995, budget: 4200000, donors: 18500, tags: ["water", "sanitation", "global", "health"], description: "Large international water access programs." },
  { id: "n10", name: "EarthAction International", country: "United States", founded: 1988, budget: 8900000, donors: 45000, tags: ["climate", "environment", "advocacy", "global"], description: "Worldwide climate advocacy and policy work." },
  { id: "n11", name: "World Education Alliance", country: "France", founded: 2001, budget: 2300000, donors: 9700, tags: ["education", "children", "global"], description: "Funds schools across 30 countries." },
  { id: "n12", name: "Women's Global Fund", country: "Netherlands", founded: 1999, budget: 1850000, donors: 8200, tags: ["women", "rights", "global", "advocacy"], description: "Grants to women-led organisations worldwide." },
  { id: "n13", name: "Riverbank Bee Sanctuary", country: "Portugal", founded: 2021, budget: 7200, donors: 38, tags: ["biodiversity", "bees", "environment", "europe"], description: "Protects native pollinators along river ecosystems." },
  { id: "n14", name: "Coral Reef Guardians", country: "Indonesia", founded: 2019, budget: 11400, donors: 72, tags: ["ocean", "biodiversity", "climate", "reef"], description: "Community-led coral restoration." },
  { id: "n15", name: "Street Cats of Istanbul", country: "Türkiye", founded: 2017, budget: 5600, donors: 41, tags: ["animals", "welfare", "europe"], description: "Sterilisation and care for urban stray cats." },
  { id: "n16", name: "Mental Health for Youth", country: "Canada", founded: 2012, budget: 240000, donors: 1100, tags: ["health", "youth", "mental", "advocacy"], description: "Counselling access for teenagers." },
  { id: "n17", name: "Desert Schools Project", country: "Morocco", founded: 2018, budget: 13800, donors: 67, tags: ["education", "children", "africa", "rural"], description: "Builds and staffs schools in remote desert regions." },
  { id: "n18", name: "Arctic Climate Watch", country: "Norway", founded: 2015, budget: 320000, donors: 1400, tags: ["climate", "research", "arctic", "environment"], description: "Independent monitoring of polar ice change." },
];

export const MICRO_BUDGET_LIMIT = 20000;
