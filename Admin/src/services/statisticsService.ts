import { statisticsData } from "@/data/statistics";

const delay = (ms = 300) => new Promise((r) => setTimeout(r, ms));

export const statisticsService = {
  async getStatistics() {
    await delay();
    return statisticsData;
  },
};
