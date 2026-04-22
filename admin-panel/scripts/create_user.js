import { createClient } from '@supabase/supabase-js'

const SUPABASE_URL = 'https://acswwfbdbeqxbmadszue.supabase.co'
const SUPABASE_SERVICE_ROLE_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFjc3d3ZmJkYmVxeGJtYWRzenVlIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3NjY2NDQ3NywiZXhwIjoyMDkyMjQwNDc3fQ.Z6A36e6M8i6x_N3N6w8i-A_ZzM-N6i8A-zN6i8A-z8A'

const serviceClient = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY)

async function createUser() {
  const { data, error } = await serviceClient.auth.admin.createUser({
    email: 'earnchipsonline@gmail.com',
    password: 'Prakash123@gutpa',
    email_confirm: true
  })

  if (error) {
    console.error('Error creating user:', error.message)
  } else {
    console.log('User created successfully:', data.user.email)
  }
}

createUser()
