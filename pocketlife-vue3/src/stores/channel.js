import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getAllChannel } from '@/api/channel'

const channelOrder = ['游戏', '旅行', '健身', '新闻', '搞笑', '影视', '美食', '科技', 'AI', '校园圈']

export const useChannelStore = defineStore('channel', () => {
  // 存储所有频道数据
  const channels = ref([])
  
  // 加载所有频道数据
  const loadChannels = async () => {
    try {
      const res = await getAllChannel()

      if (res.success && res.data) {
        const channelsByName = new Map(res.data.map(channel => [channel.name, channel]))
        channels.value = channelOrder.map(name => channelsByName.get(name)).filter(Boolean)
      }
    } catch (error) {
      console.error('加载频道数据失败:', error)
    }
  }
  
  // 根据ID获取频道名称
  const getChannelNameById = (id) => {
    const channel = channels.value.find(item => item.id === id)
    return channel ? channel.name : ''
  }
  
  return {
    channels,
    loadChannels,
    getChannelNameById
  }
}) 
