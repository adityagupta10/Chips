import { useState, useEffect } from 'react'
import { supabase } from './supabase'

export default function App() {
  const [session, setSession] = useState(null)
  const [email, setEmail] = useState('admin@resugrow.com')
  const [password, setPassword] = useState('Prakash123@gutpa')
  const [tasks, setTasks] = useState([])
  const [newTask, setNewTask] = useState({ title: '', reward: '', description: '' })
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

  useEffect(() => {
    if (session) {
      fetchTasks()
    }
  }, [session])

  async function fetchTasks() {
    const { data, error } = await supabase.from('tasks').select('*')
    if (error) console.error('Error fetching tasks:', error)
    else setTasks(data || [])
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
    const { error } = await supabase.from('tasks').insert([newTask])
    if (error) alert(error.message)
    else {
      setNewTask({ title: '', reward: '', description: '' })
      fetchTasks()
    }
    setLoading(false)
  }

  if (!session) {
    return (
      <div style={styles.container}>
        <div style={styles.card}>
          <h2>Chips Admin Login</h2>
          <form onSubmit={handleLogin} style={styles.form}>
            <input 
              style={styles.input}
              type="email" 
              placeholder="Email" 
              value={email} 
              onChange={e => setEmail(e.target.value)} 
            />
            <input 
              style={styles.input}
              type="password" 
              placeholder="Password" 
              value={password} 
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
        <h1>Chips Admin Dashboard</h1>
        <button onClick={() => supabase.auth.signOut()} style={styles.logoutBtn}>Logout</button>
      </header>
      
      <main style={styles.main}>
        <section style={styles.section}>
          <h2>Add New Task</h2>
          <form onSubmit={addTask} style={styles.addTaskForm}>
            <input 
              style={styles.input}
              placeholder="Task Title" 
              value={newTask.title} 
              onChange={e => setNewTask({...newTask, title: e.target.value})} 
            />
            <input 
              style={styles.input}
              placeholder="Reward (e.g. 100 Chips)" 
              value={newTask.reward} 
              onChange={e => setNewTask({...newTask, reward: e.target.value})} 
            />
            <textarea 
              style={{...styles.input, height: '100px'}}
              placeholder="Description" 
              value={newTask.description} 
              onChange={e => setNewTask({...newTask, description: e.target.value})} 
            />
            <button style={styles.button} type="submit" disabled={loading}>
              {loading ? 'Adding...' : 'Add Task'}
            </button>
          </form>
        </section>

        <section style={styles.section}>
          <h2>Existing Tasks</h2>
          <div style={styles.taskList}>
            {tasks.map(task => (
              <div key={task.id} style={styles.taskItem}>
                <strong>{task.title}</strong> - <span style={{color: '#ffd700'}}>{task.reward}</span>
                <p style={{fontSize: '0.9em', color: '#a1a1aa'}}>{task.description}</p>
              </div>
            ))}
          </div>
        </section>
      </main>
    </div>
  )
}

const styles = {
  container: { display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' },
  card: { background: '#1e1e22', padding: '2rem', borderRadius: '1rem', width: '300px', textAlign: 'center' },
  form: { display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '1rem' },
  input: { padding: '0.75rem', borderRadius: '0.5rem', border: '1px solid #2a2a30', background: '#121214', color: 'white' },
  button: { padding: '0.75rem', borderRadius: '0.5rem', border: 'none', background: '#8a2be2', color: 'white', fontWeight: 'bold', cursor: 'pointer' },
  dashboard: { padding: '2rem' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #2a2a30', paddingBottom: '1rem' },
  logoutBtn: { padding: '0.5rem 1rem', background: '#ff4b2b', color: 'white', border: 'none', borderRadius: '0.5rem', cursor: 'pointer' },
  main: { display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '2rem', marginTop: '2rem' },
  section: { background: '#1e1e22', padding: '1.5rem', borderRadius: '1rem' },
  addTaskForm: { display: 'flex', flexDirection: 'column', gap: '1rem' },
  taskList: { display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '1rem' },
  taskItem: { padding: '1rem', background: '#2a2a30', borderRadius: '0.5rem' }
}
