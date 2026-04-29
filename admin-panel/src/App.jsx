import { useState, useEffect } from 'react'
import { supabase } from './supabase'

function ensureMetaTag(name, content) {
  let tag = document.head.querySelector(`meta[name="${name}"]`)
  if (!tag) {
    tag = document.createElement('meta')
    tag.setAttribute('name', name)
    document.head.appendChild(tag)
  }
  tag.setAttribute('content', content)
}

export default function App() {
  const [session, setSession] = useState(null)
  const [email, setEmail] = useState('admin@resugrow.com')
  const [password, setPassword] = useState('Prakash123@gutpa')
  const [tasks, setTasks] = useState([])
  const [newTask, setNewTask] = useState({ title: '', chip_reward: '', description: '', category: 'video_ad' })
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    supabase.auth.getSession().then(({ data: { session } }) => {
      setSession(session)
    })

    const { data: { subscription } } = supabase.auth.onAuthStateChange((_event, session) => {
      setSession(session)
    })

    return () => subscription.unsubscribe()
  }, [])

  const [activeTab, setActiveTab] = useState('tasks')
  const [redemptions, setRedemptions] = useState([])
  const [notification, setNotification] = useState({ title: '', body: '', target_user_id: '' })

  useEffect(() => {
    if (session) {
      fetchTasks()
      fetchRedemptions()
    }
  }, [session])

  useEffect(() => {
    const titleMap = {
      tasks: 'Tasks',
      redemptions: 'Redemptions',
      notifications: 'Broadcast'
    }

    if (!session) {
      document.title = 'Chips Admin Login'
      ensureMetaTag(
        'description',
        'Secure login for the Chips operations dashboard.'
      )
    } else {
      const activeLabel = titleMap[activeTab] || 'Dashboard'
      document.title = `Chips Admin | ${activeLabel}`
      ensureMetaTag(
        'description',
        'Secure operations dashboard for Chips rewards, task management, redemptions, and user notifications.'
      )
    }
  }, [activeTab, session])

  async function sendNotification(e) {
    e.preventDefault()
    setLoading(true)
    
    const { data, error } = await supabase.functions.invoke('broadcast-notification', {
      body: {
        title: notification.title,
        body: notification.body,
        screen: notification.screen,
        target_user_id: notification.target_user_id || null
      }
    })

    if (error) {
      alert('Edge Function Error: ' + error.message)
    } else {
      alert(`Success! Sent to ${data.count || 0} devices.`)
      setNotification({ title: '', body: '', target_user_id: '', screen: '' })
    }
    setLoading(false)
  }

  async function fetchTasks() {
    const { data, error } = await supabase.from('tasks').select('*').order('created_at', { ascending: false })
    if (error) console.error('Error fetching tasks:', error)
    else setTasks(data || [])
  }

  async function fetchRedemptions() {
    const { data, error } = await supabase.from('redemptions').select('*').order('created_at', { ascending: false })
    if (error) console.error('Error fetching redemptions:', error)
    else setRedemptions(data || [])
  }

  async function updateRedemption(id, status) {
    let failure_reason = ''
    if (status === 'FAILED') {
      failure_reason = prompt('Reason for failure:')
      if (!failure_reason) return
    }

    const { error } = await supabase.from('redemptions')
      .update({ status, processed_at: new Date(), failure_reason })
      .eq('id', id)
    
    if (error) alert(error.message)
    else fetchRedemptions()
  }

  async function handleLogin(e) {
    e.preventDefault()
    setLoading(true)
    const { error } = await supabase.auth.signInWithPassword({ email, password })
    if (error) alert(error.message)
    setLoading(false)
  }

  async function addTask(e) {
    e.preventDefault()
    setLoading(true)
    const { error } = await supabase.from('tasks').insert([{
      ...newTask,
      chip_reward: parseInt(newTask.chip_reward)
    }])
    if (error) alert(error.message)
    else {
      setNewTask({ title: '', chip_reward: '', description: '', category: 'video_ad' })
      fetchTasks()
    }
    setLoading(false)
  }

  if (!session) {
    return (
      <div style={styles.container}>
        <div style={styles.card}>
          <h2 style={{color: '#ffd700'}}>Chips Admin</h2>
          <form onSubmit={handleLogin} style={styles.form}>
            <input 
              style={styles.input}
              type="email" 
              placeholder="Email" 
              value={email} 
              autoComplete="username"
              onChange={e => setEmail(e.target.value)} 
            />
            <input 
              style={styles.input}
              type="password" 
              placeholder="Password" 
              value={password} 
              autoComplete="current-password"
              onChange={e => setPassword(e.target.value)} 
            />
            <button style={styles.button} type="submit" disabled={loading}>
              {loading ? 'Logging in...' : 'Login'}
            </button>
          </form>
        </div>
      </div>
    )
  }

  return (
    <div style={styles.dashboard}>
      <header style={styles.header}>
        <div style={{display: 'flex', alignItems: 'center', gap: '2rem'}}>
          <h1 style={{color: '#ffd700', margin: 0}}>Chips Admin</h1>
          <nav style={{display: 'flex', gap: '1rem'}}>
            <button 
              onClick={() => setActiveTab('tasks')}
              style={{...styles.tabBtn, borderBottom: activeTab === 'tasks' ? '3px solid #8a2be2' : 'none'}}
            >Tasks</button>
            <button 
              onClick={() => setActiveTab('redemptions')}
              style={{...styles.tabBtn, borderBottom: activeTab === 'redemptions' ? '3px solid #8a2be2' : 'none'}}
            >Redemptions ({redemptions.filter(r => r.status === 'PENDING').length})</button>
            <button 
              onClick={() => setActiveTab('notifications')}
              style={{...styles.tabBtn, borderBottom: activeTab === 'notifications' ? '3px solid #8a2be2' : 'none'}}
            >Broadcast</button>
          </nav>
        </div>
        <button onClick={() => supabase.auth.signOut()} style={styles.logoutBtn}>Logout</button>
      </header>
      
      <main style={styles.mainContent}>
        {activeTab === 'tasks' ? (
          <div style={styles.tasksGrid}>
            <section style={styles.section}>
              <h3>Add New Task</h3>
              <form onSubmit={addTask} style={styles.addTaskForm}>
                <input style={styles.input} placeholder="Task Title" value={newTask.title} onChange={e => setNewTask({...newTask, title: e.target.value})} />
                <input style={styles.input} placeholder="Chip Reward" type="number" value={newTask.chip_reward} onChange={e => setNewTask({...newTask, chip_reward: e.target.value})} />
                <select style={styles.input} value={newTask.category} onChange={e => setNewTask({...newTask, category: e.target.value})}>
                  <option value="video_ad">Video Ad</option>
                  <option value="survey">Survey</option>
                  <option value="app_install">App Install</option>
                  <option value="mini_game">Mini Game</option>
                  <option value="daily_checkin">Daily Check-in</option>
                </select>
                <input style={styles.input} placeholder="Action URL" value={newTask.action_url} onChange={e => setNewTask({...newTask, action_url: e.target.value})} />
                <textarea style={{...styles.input, height: '80px'}} placeholder="Description" value={newTask.description} onChange={e => setNewTask({...newTask, description: e.target.value})} />
                <button style={styles.button} type="submit" disabled={loading}>Add Task</button>
              </form>
            </section>

            <section style={styles.section}>
              <h3>Task Management</h3>
              <div style={styles.taskList}>
                {tasks.map(task => (
                  <div key={task.id} style={styles.taskItem}>
                    <div style={{display: 'flex', justifyContent: 'space-between'}}>
                      <strong>{task.title}</strong>
                      <span style={{color: '#ffd700'}}>{task.chip_reward} Chips</span>
                    </div>
                    <div style={{fontSize: '0.8em', color: '#8a2be2', marginTop: '4px'}}>{task.category}</div>
                  </div>
                ))}
              </div>
            </section>
          </div>
        ) : activeTab === 'redemptions' ? (
          <section style={styles.section}>
            <h3>Redemption Queue</h3>
            <table style={styles.table}>
              <thead>
                <tr>
                  <th>User ID</th>
                  <th>Type</th>
                  <th>Destination</th>
                  <th>Amount</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {redemptions.map(red => (
                  <tr key={red.id} style={{borderBottom: '1px solid #2a2a30'}}>
                    <td style={{fontSize: '0.8em'}}>{red.user_id.slice(0, 8)}...</td>
                    <td>{red.type}</td>
                    <td>{red.destination}</td>
                    <td style={{color: '#10b981', fontWeight: 'bold'}}>₹{red.amount_inr}</td>
                    <td>
                       <span style={{
                         padding: '4px 8px', borderRadius: '4px', fontSize: '0.7em', fontWeight: 'bold',
                         background: red.status === 'COMPLETED' ? '#065f46' : red.status === 'PENDING' ? '#92400e' : '#7f1d1d'
                       }}>
                         {red.status}
                       </span>
                    </td>
                    <td>
                      {red.status === 'PENDING' && (
                        <div style={{display: 'flex', gap: '0.5rem'}}>
                          <button onClick={() => updateRedemption(red.id, 'COMPLETED')} style={{background: '#10b981', border: 'none', color: 'white', padding: '4px 8px', borderRadius: '4px', cursor: 'pointer'}}>Pay</button>
                          <button onClick={() => updateRedemption(red.id, 'FAILED')} style={{background: '#ef4444', border: 'none', color: 'white', padding: '4px 8px', borderRadius: '4px', cursor: 'pointer'}}>Fail</button>
                        </div>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </section>
        ) : (
          <section style={styles.section}>
            <h3>Global Broadcast (FCM)</h3>
            <p style={{fontSize: '0.8em', color: '#8a2be2', marginBottom: '1.5rem'}}>
              Send push notifications directly to user devices via Firebase.
            </p>
            <form onSubmit={sendNotification} style={{...styles.addTaskForm, maxWidth: '600px'}}>
              <input style={styles.input} placeholder="Notification Title (e.g. 🔥 2x Chips Ready!)" value={notification.title} onChange={e => setNotification({...notification, title: e.target.value})} required />
              <textarea style={{...styles.input, height: '100px'}} placeholder="Notification Body" value={notification.body} onChange={e => setNotification({...notification, body: e.target.value})} required />
              
              <div style={{display: 'flex', gap: '1rem'}}>
                <select style={{...styles.input, flex: 1}} value={notification.screen || ''} onChange={e => setNotification({...notification, screen: e.target.value})}>
                  <option value="">No Deep Link</option>
                  <option value="task_feed">Task Feed</option>
                  <option value="spin_wheel">Spin Wheel</option>
                  <option value="profile">Profile</option>
                </select>
                <input style={{...styles.input, flex: 2}} placeholder="Specific User ID (Optional)" value={notification.target_user_id} onChange={e => setNotification({...notification, target_user_id: e.target.value})} />
              </div>

              <button style={{...styles.button, background: '#ffd700', color: '#000'}} type="submit" disabled={loading}>
                {loading ? 'Sending...' : '🚀 Broadcast Now'}
              </button>
            </form>
          </section>
        )}
      </main>
    </div>
  )
}

const styles = {
  container: { display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', background: '#0a0a0c' },
  card: { background: '#1e1e22', padding: '2rem', borderRadius: '1rem', width: '300px', textAlign: 'center' },
  form: { display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '1rem' },
  input: { padding: '0.75rem', borderRadius: '0.5rem', border: '1px solid #2a2a30', background: '#121214', color: 'white' },
  button: { padding: '0.75rem', borderRadius: '0.5rem', border: 'none', background: '#8a2be2', color: 'white', fontWeight: 'bold', cursor: 'pointer' },
  dashboard: { padding: '0', background: '#0a0a0c', minHeight: '100vh', color: 'white' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: '#1e1e22', padding: '1rem 2rem' },
  tabBtn: { background: 'none', border: 'none', color: 'white', padding: '1rem', cursor: 'pointer', fontWeight: 'bold' },
  logoutBtn: { padding: '0.5rem 1rem', background: '#ff4b2b', color: 'white', border: 'none', borderRadius: '0.5rem', cursor: 'pointer' },
  mainContent: { padding: '2rem' },
  tasksGrid: { display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem' },
  section: { background: '#1e1e22', padding: '1.5rem', borderRadius: '1rem', overflowX: 'auto' },
  addTaskForm: { display: 'flex', flexDirection: 'column', gap: '1rem' },
  taskList: { display: 'flex', flexDirection: 'column', gap: '0.5rem' },
  taskItem: { padding: '1rem', background: '#2a2a30', borderRadius: '0.5rem' },
  table: { width: '100%', borderCollapse: 'collapse', textAlign: 'left', marginTop: '1rem' }
}
